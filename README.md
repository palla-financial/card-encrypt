:credit_card: Card RSA Encryption
-------------

Thank you for choosing Palla!  :money_with_wings:

In this repository you'll find the encryption code necessary to enroll debit cards in the Palla API. There are examples in Node.js and GO.

If you need an example for your stack, please contact us for assistance.

## - Releases

The binaries under releases are built from the Go source. These are safe to use in production as part of your card enrollment flow.

**Before running a binary, verify it is an authentic, untampered Palla build.** Each release ships a signed SHA-256 checksum manifest (`SHA256SUMS` + `SHA256SUMS.sig`) alongside Palla's public signing key (`palla-card-encrypt-pubkey.pem`). The manifest is signed by a key held in Palla's AWS KMS — the private key cannot be exported, and signing is access-controlled and audited. See **[VERIFICATION.md](./VERIFICATION.md)** for full step-by-step instructions.

Quick verify (macOS/Linux, requires only `openssl`):

```
openssl dgst -sha256 -verify palla-card-encrypt-pubkey.pem -signature SHA256SUMS.sig SHA256SUMS   # prints: Verified OK
shasum -a 256 -c SHA256SUMS                                                                        # Linux: sha256sum -c SHA256SUMS
```

Only use a binary if both checks succeed.

Usage:

```
./palla-rsa-encrypt -key my_key.pem -card 4000056655665556 -expr 202901
```

## - Node.js

To run the code:

```
npm install
node index.js
```

To use, edit `index.js` to contain the correct location of the public key you were provided, as well as the card data.

## - GO

To build the code:

```
go build
```

You can cross-compile to other operating systems and system architectures. Here are some examples.

https://freshman.tech/snippets/go/cross-compile-go-programs/