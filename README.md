:credit_card: Card RSA Encryption
-------------

Thank you for choosing Palla!  :money_with_wings:

In this repository you'll find the encryption code necessary to enroll debit cards in the Palla API. There are examples in Node.js and GO.

If you need an example for your stack, please contact us for assistance.

## - Releases

The binaries under releases are built from the Go source. These are safe to use in production as part of your card enrollment flow, but please verify the binary and checksum.

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