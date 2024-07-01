const https = require('https');
const {URL} = require("url");

function request(options, dataString){
    return new Promise(function(resolve, reject) {
        const req = https.request(options, res => {
            let s = ""
    
            res.on('data', d => {
                s += d;
            });

            res.on('end', d => {
                const response = {
                    statusCode: res.statusCode
                }
                
                if (res.statusCode >= 400) {
                    return reject(response)
                }

                response.data = JSON.parse(s)

                return resolve(response)
            })
        });
    
        req.on('error', error => {
            return reject({
                error: error
            })
        });
        
        if (dataString) {
            req.write(dataString)
        }
    
        req.end();
    })
}

function parseReq(method, url) {
    const u = new URL(url);
    const res = {
        options: {
            port: 443,
            method: method,
            hostname: u.hostname,
            path: u.pathname,
            headers: {}
        },
        data: undefined
    }
    return res
}

function GET(url){
    const {options} = parseReq("GET", url)
    return request(options)
}

module.exports = {
    GET
}