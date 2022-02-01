CARD ENCRYPT
-------------

Thank you for choosing Palla!

Please find the following in this repository:

## --> Releases

The binaries in release are built from the Go source.

Usage:

```
./palla-rsa-encrypt -key my_key.pem -card 4000056655665556 -expr 202901
```

## --> Sample node.js code

To run the code:

```
npm install
node index.js
```

Edit `index.js` to contain the correct location of the public key you were provided, as well as the card data.

## --> Sample Go code

To build the code:

```
go build
```

You can cross-compile to other operating systems and system architectures.

https://freshman.tech/snippets/go/cross-compile-go-programs/