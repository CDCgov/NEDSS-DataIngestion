const url = 'https://6cfe-2607-fb91-1590-2508-48e3-8e4b-c4f4-9d28.ngrok-free.app/token';

const config = {
    'method': 'POST',
    headers: {
        'Content-Type': 'text/plain',
        'Accept': '*/*',
        'msgType': 'HL7',
        'Authorization': 'Basic ZGl0ZWFtYWRtaW46dGVtcDEyMw==',
        // 'connection': 'keep-alive',
    }
};
// const response = async() => {
//     fetch(url, config)
//         .then(response => {
//             if(response.ok) {
//                 // process.env.DI_SERVICE_TOKEN = response.text();
//                 token = await response.text();
//                 console.log(response.text());
//             } else {
//                 // process.env.DI_SERVICE_TOKEN = 'Error from API';
//                 console.log(response.statusText());
//             }
//         });
// }

var jwtToken;

//es3
// function getResponse() {
//     var xhr = new XMLHttpRequest();
//     xhr.open('POST', url, true);
//     xhr.onreadystatechange = function () {
//         if(xhr.readyState === 4) {
//             if(xhr.status === 200) {
//                 jwtToken = xhr.responseText;
//                 console.log('Token is...', jwtToken);
//             }
//             else {
//                 console.error('Error...', xhr.status);
//             }
//         }
//     };
//     xhr.send();
// }

// function getResponse() {
//     var xhr = new Packages.org.mozilla.javascript.XMLHttpRequest();
//     xhr.open(config.method, url, true);
//
//     for(var header in config.headers) {
//         xhr.setRequestHeader(header, config.headers[header]);
//     }
//
//     xhr.onreadystatechange = function () {
//         if(xhr.readyState === xhr.DONE) {
//             if(xhr.status === 200) {
//                 jwtToken = xhr.responseText;
//                 process.env.DI_SERVICE_TOKEN = jwtToken;
//             }
//             else {
//                 process.env.DI_SERVICE_TOKEN = 'Error';
//             }
//         }
//     };
//     xhr.send();
// }

function getResponse() {
    var url = 'https://6cfe-2607-fb91-1590-2508-48e3-8e4b-c4f4-9d28.ngrok-free.app/token';

    var config = {
        'method': 'POST',
        headers: {
            'Content-Type': 'text/plain',
            'Accept': '*/*',
            'msgType': 'HL7',
            'Authorization': 'Basic ZGl0ZWFtYWRtaW46dGVtcDEyMw=='
        }
    };

    var javaUrl = new Packages.java.net.URL(url);
    var connection = javaUrl.openConnection();
    connection.setRequestMethod(config.method);

    for(var header in config.headers) {
         connection.setRequestProperty(header, config.headers[header])
    }

    if(connection.getResponseCode() === Packages.java.net.HttpURLConnection.HTTP_OK) {
        var inputStream = connection.getInputStream();
        var scanner = new Packages.java.util.Scanner(inputStream).useDelimiter("\\A");
        jwtToken = scanner.hasNext() ? scanner.hasNext() : "Error";
        scanner.close();
    }
    else {
        jwtToken = connection.getResponseCode();
    }
    connection.disconnect();
}


getResponse();

// function getResponse() {
//     fetch(url, config)
//         .then(response => {
//             if(response.ok) {
//                 console.log('Response text is...', response.text());
//             } else {
//                 console.log('Error in API');
//             }
//         })
//         .then(token => {
//             jwtToken = token;
//             console.log('Token is...', jwtToken);
//         })
//         .catch(error => {
//             console.log('Error...', error);
//         });
// }

// async function getResponse() {
//     try {
//         const response = await fetch(url, config);
//         if (response.ok) {
//             jwtToken = await response.text();
//             console.log('Token is...', jwtToken);
//         } else {
//             console.log('Error in API');
//         }
//     } catch (error) {
//         console.error(error);
//     }
// }
//
// getResponse();



var jwtToken;

function getResponse() {
    var url = 'https://6cfe-2607-fb91-1590-2508-48e3-8e4b-c4f4-9d28.ngrok-free.app/token';

    var config = {
        'method': 'POST',
        headers: {
            'Content-Type': 'text/plain',
            'Accept': '*/*',
            'msgType': 'HL7',
            'Authorization': 'Basic ZGl0ZWFtYWRtaW46dGVtcDEyMw=='
        }
    };

    var javaUrl = new Packages.java.net.URL(url);
    var connection = javaUrl.openConnection();
    connection.setRequestMethod(config.method);

    for(var header in config.headers) {
        connection.setRequestProperty(header, config.headers[header])
    }

    if(connection.getResponseCode() === Packages.java.net.HttpURLConnection.HTTP_OK) {
        var inputStream = connection.getInputStream();
        var scanner = new Packages.java.util.Scanner(inputStream).useDelimiter("\\A");
        jwtToken = scanner.hasNext() ? scanner.hasNext() : "Error";
        java.lang.System.out.println('JWT Token is...' + jwtToken);
        // javaStatic(java.lang.System, 'setProperty', 'DI_SERVICE_TOKEN', jwtToken);
        Packages.java.lang.System.setProperty('DI_SERVICE_TOKEN', jwtToken);
        // setProperty('DI_SERVICE_TOKEN', jwtToken);
        scanner.close();
    }
    else {
        jwtToken = connection.getResponseCode();
    }
    connection.disconnect();
}

getResponse();