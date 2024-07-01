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
	"io"
	"log"
	"os"
	"strings"
)

var (
	keyFileFlag = flag.String("key-file", "", "(CONDITIONAL) The file path of RSA public key in .pem format")
	keyDataFlag = flag.String("key-data", "", "(CONDITIONAL) The contents of RSA public key in .pem format.")
	cardFlag    = flag.String("card", "", "(REQUIRED) The card number to encrypt, ex. 4000056655665556")
	expFlag     = flag.String("expr", "", "(REQUIRED) The card expiration in YYYYMM format, ex. 203208")
	cvvFlag     = flag.String("cvv", "", "(OPTIONAL) The card CVV code")
)

func main() {
	flag.Parse()

	if *cardFlag == "" || *expFlag == "" {
		flag.Usage()
		return
	}

	// Load key from stdin, key-data flag or key-file flag
	keyBytes, err := LoadKey(*keyDataFlag, *keyFileFlag)
	if err != nil {
		log.Fatal(err)
	}

	// Parse key data
	key, err := ParseKey(keyBytes)
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
	fmt.Print(encoded)
}

func LoadKey(keydata string, keyfilename string) ([]byte, error) {
	if keydata != "" {
		return []byte(keydata), nil
	}

	keyFile, err := os.Open(keyfilename)
	if err != nil {
		return nil, err
	}

	if stat, err := keyFile.Stat(); err != nil {
		return nil, err
	} else if stat.Mode()&os.ModeCharDevice != 0 {
		return nil, fmt.Errorf("must set key flags")
	}

	return io.ReadAll(keyFile)
}

func ParseKey(keyBytes []byte) (*rsa.PublicKey, error) {
	// Decode PEM
	block, _ := pem.Decode(keyBytes)
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
