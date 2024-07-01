const fs = require("fs")
const NodeRSA = require("node-rsa") // https://www.npmjs.com/package/node-rsa
const {GET} = require("./get");

async function run(card, expr, cvv) {
  // Get keyfile from Palla bucket
  // Sandbox: https://palla-public-keys.s3.us-east-2.amazonaws.com/card-encrypt/sandbox.json
  // Production: https://palla-public-keys.s3.us-east-2.amazonaws.com/card-encrypt/prod.json
  const res = await GET("https://palla-public-keys.s3.us-east-2.amazonaws.com/card-encrypt/sandbox.json")
  let keyString = res.data.key.replace(/\\n/g, "\n")

  // Parse Key with NodeRSA lib
  const key = new NodeRSA(keyString, 'pkcs8-public-pem', {
      environment: 'browser',
      encryptionScheme: {
          hash: 'sha256',
        },
  })
  
  // Format card data into correct format, pipe-delineated string
  // Full card number
  // Expiration in YYYYMM format
  // CVV Code
  const cardData = [card, expr, cvv].join("|")
  
  // Encrypt card data
  const encrypted = key.encrypt(cardData)
  
  // Encode with URL Safe Base64
  const encoded = encrypted.toString("base64url")
  
  // Output
  console.log(encoded)
}

run("4000056655665556", "202412", "111")
