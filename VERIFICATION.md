## Verifying card-encrypt release binaries

Each release ships a signed checksum manifest so you can confirm a binary is an
authentic, untampered Palla build before using it in your card-enrollment flow.

The manifest is signed by a key held in Palla's AWS KMS (the private key cannot be
exported; signing is access-controlled and audited). You verify with the published
public key using `openssl`, which ships on macOS/Linux by default.

Release assets:
- `SHA256SUMS` — SHA-256 checksums of every published binary
- `SHA256SUMS.sig` — ECDSA-P256/SHA-256 signature of `SHA256SUMS` (DER)
- `palla-card-encrypt-pubkey.pem` — Palla's public signing key

### One-time: pin Palla's public signing key
Confirm the key fingerprint matches the value Palla published out-of-band before
trusting it:
```sh
openssl pkey -pubin -in palla-card-encrypt-pubkey.pem -outform DER | openssl dgst -sha256
# expected: 21352d6166664f95c8facafa2de442f5e4ac89d1822ee5479334de57b8e02a74
```

### Per release: verify the manifest, then check your binary
```sh
# 1. Confirm the manifest was signed by Palla
openssl dgst -sha256 -verify palla-card-encrypt-pubkey.pem \
  -signature SHA256SUMS.sig SHA256SUMS          # prints: Verified OK

# 2. Confirm your binary matches the manifest
sha256sum -c SHA256SUMS                          # macOS: shasum -a 256 -c SHA256SUMS
```
Only use a binary if both steps succeed. A failed signature or checksum means the
file may have been tampered with — do not run it; contact Palla.
