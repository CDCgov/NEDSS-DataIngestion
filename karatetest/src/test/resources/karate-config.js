function() {
  var env = karate.env;
  if (!env) {
    env = 'test';
  }

  var config = {
    connectTimeout: karate.properties['connectTimeout'],
    readTimeout: karate.properties['readTimeout'],
    retryCount: karate.properties['retryCount'],
    retryInterval: karate.properties['retryInterval']
  };

  if (env == 'test') {

    config.apiurl = karate.properties['test.apiurl'];
    config.apiusername = karate.properties['test.apiusername'];
    config.apipassword = karate.properties['test.apipassword'];
    config.wrongapiurl = karate.properties['test.wrongapiurl'];
    config.registrationapiurl = karate.properties['test.registrationapiurl'];
    config.tokenurl = karate.properties['test.tokenurl'];
    config.checkerrorurl = karate.properties['test.checkerrorurl'];
    config.checkstatusurl = karate.properties['test.checkstatusurl'];


  } else if (env == 'dev') {

    config.apiurl = karate.properties['dev.apiurl'];
    config.apiusername = karate.properties['dev.apiusername'];
    config.apipassword = karate.properties['dev.apipassword'];
    config.wrongapiurl = karate.properties['dev.wrongapiurl'];
    config.registrationapiurl = karate.properties['dev.registrationapiurl'];
    config.tokenurl = karate.properties['dev.tokenurl'];
    config.checkerrorurl = karate.properties['dev.checkerrorurl'];
    config.checkstatusurl = karate.properties['dev.checkstatusurl'];


  } else {

    return;
  }

  return config;
}