const fs = require("fs")
const NodeRSA = require("node-rsa") // https://www.npmjs.com/package/node-rsa

// Read in key file
const keyBytes = fs.readFileSync("./YOUR_KEY_FILE_HERE.pem")

// Parse Key with NodeRSA lib
const key = new NodeRSA(keyBytes, 'pkcs8-public-pem', {
    environment: 'browser',
    encryptionScheme: {
        hash: 'sha256',
      },
})

// Format card data into correct format, pipe-delineated string
// Full card number
// Expiration in YYYYMM format
// CVV Code
const card = ["4000056655665556", "202412", "111"].join("|")

// Encrypt card data
const encrypted = key.encrypt(card)

// Encode with URL Safe Base64
const encoded = encrypted.toString("base64url")

// Output
console.log(encoded)