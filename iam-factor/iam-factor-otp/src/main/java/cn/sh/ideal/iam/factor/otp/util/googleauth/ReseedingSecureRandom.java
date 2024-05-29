package cn.sh.ideal.iam.factor.otp.util.googleauth;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ReseedingSecureRandom {
    private static final int MAX_OPERATIONS = 1_000_000;
    private final Lock lock = new ReentrantLock();
    private final String provider;
    private final String algorithm;
    private final AtomicInteger count = new AtomicInteger(0);
    private volatile SecureRandom secureRandom;

    @SuppressWarnings("UnusedDeclaration")
    ReseedingSecureRandom() {
        this.algorithm = null;
        this.provider = null;

        buildSecureRandom();
    }

    @SuppressWarnings("UnusedDeclaration")
    ReseedingSecureRandom(String algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm cannot be null.");
        }

        this.algorithm = algorithm;
        this.provider = null;

        buildSecureRandom();
    }

    ReseedingSecureRandom(String algorithm, String provider) {
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm cannot be null.");
        }

        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null.");
        }

        this.algorithm = algorithm;
        this.provider = provider;

        buildSecureRandom();
    }

    private void buildSecureRandom() {
        try {
            if (this.algorithm == null && this.provider == null) {
                this.secureRandom = new SecureRandom();
            } else if (this.provider == null) {
                this.secureRandom = SecureRandom.getInstance(this.algorithm);
            } else {
                if (this.algorithm != null) {
                    this.secureRandom = SecureRandom.getInstance(this.algorithm, this.provider);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new GoogleAuthenticatorException(
                    String.format(
                            "Could not initialise SecureRandom with the specified algorithm: %s. " +
                                    "Another provider can be chosen setting the %s system property.",
                            this.algorithm,
                            GoogleAuthenticator.RNG_ALGORITHM
                    ), e
            );
        } catch (NoSuchProviderException e) {
            throw new GoogleAuthenticatorException(
                    String.format(
                            "Could not initialise SecureRandom with the specified provider: %s. " +
                                    "Another provider can be chosen setting the %s system property.",
                            this.provider,
                            GoogleAuthenticator.RNG_ALGORITHM_PROVIDER
                    ), e
            );
        }
    }

    void nextBytes(byte[] bytes) {
        if (count.incrementAndGet() > MAX_OPERATIONS) {
            lock.lock();
            try {
                if (count.get() > MAX_OPERATIONS) {
                    buildSecureRandom();
                    count.set(0);
                }
            } finally {
                lock.unlock();
            }
        }

        this.secureRandom.nextBytes(bytes);
    }
}
