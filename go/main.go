package main

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"strings"
)

var (
	keyFlag  = flag.String("key", "", "The provided public key in .pem format")
	cardFlag = flag.String("card", "", "The card number to encrypt, ex. 4000056655665556")
	expFlag  = flag.String("expr", "", "The card expiration in YYYYMM format, ex. 202408")
	cvvFlag  = flag.String("cvv", "000", "The card CVV code")
)

func main() {
	flag.Parse()

	if *keyFlag == "" || *cardFlag == "" || *expFlag == "" {
		flag.Usage()
		return
	}

	// Import and parse key
	key, err := LoadKey(*keyFlag)
	if err != nil {
		log.Fatal(err)
	}

	// Format card data into correct format, pipe-delineated string
	// Full card number
	// Expiration in YYYYMM format
	// CVV Code
	cardMessage := FormatCard(*cardFlag, *expFlag, *cvvFlag)

	// Encrypt card data
	encrypted, err := rsa.EncryptOAEP(crypto.SHA256.New(), rand.Reader, key, []byte(cardMessage), nil)
	if err != nil {
		log.Fatal(err)
	}

	// Encode with URL Safe Base64
	encoded := base64.URLEncoding.EncodeToString(encrypted)

	// Output
	fmt.Println(encoded)
}

func LoadKey(filename string) (*rsa.PublicKey, error) {
	// Read key file
	bytes, err := ioutil.ReadFile(filename)
	if err != nil {
		return nil, err
	}

	// Decode PEM
	block, _ := pem.Decode(bytes)
	if block == nil || block.Type != "PUBLIC KEY" {
		return nil, fmt.Errorf("failed to decode PEM block containing public key")
	}

	// Parse public key
	pub, err := x509.ParsePKIXPublicKey(block.Bytes)
	if err != nil {
		return nil, err
	}

	// Cast returned interface to *rsa.PublicKey
	key, ok := pub.(*rsa.PublicKey)
	if !ok {
		return nil, fmt.Errorf("invalid public key")
	}

	return key, nil
}

func FormatCard(number string, exp string, code string) string {
	data := []string{
		number,
		exp,
		code,
	}
	return strings.Join(data, "|")
}
