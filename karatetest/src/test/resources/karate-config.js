function() {
  var env = karate.env;
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'test';
  }

  var config = {
    connectTimeout: karate.properties['connectTimeout'],
    readTimeout: karate.properties['readTimeout'],
    retryCount: karate.properties['retryCount'],
    retryInterval: karate.properties['retryInterval']
  };

  karate.log('config object:', JSON.stringify(config));

  if (env == 'test') {
    config.apiurl = karate.properties['test.apiurl'];
    config.username = karate.properties['test.username'];
    config.password = karate.properties['test.password'];
    config.url = karate.properties['test.url'];
    config.driverClassName = karate.properties['test.driverClassName'];
    config.bootstrapServers = karate.properties['test.bootstrapServers'];
    config.groupId = karate.properties['test.groupId'];
    config.apiusername = karate.properties['test.apiusername'];
    config.apipassword = karate.properties['test.apipassword'];
    config.nbsinterfaceusername = karate.properties['test.nbsinterfaceusername'];
    config.nbsinterfacepwd = karate.properties['test.nbsinterfacepwd'];
    config.nbsdburl = karate.properties['test.nbsdburl'];
    config.nbsdriverclsName = karate.properties['test.nbsdriverclsName'];
    config.nbsurl = karate.properties['test.nbsurl'];
    config.wrongapiurl = karate.properties['test.wrongapiurl'];


  } else if (env == 'dev') {
    config.apiurl = karate.properties['dev.apiurl'];
    config.username = karate.properties['dev.username'];
    config.password = karate.properties['dev.password'];
    config.url = karate.properties['dev.url'];
    config.driverClassName = karate.properties['dev.driverClassName'];
    config.bootstrapServers = karate.properties['dev.bootstrapServers'];
    config.groupId = karate.properties['dev.groupId'];
    config.apiusername = karate.properties['dev.apiusername'];
    config.apipassword = karate.properties['dev.apipassword'];
    config.nbsinterfaceusername = karate.properties['dev.nbsinterfaceusername'];
    config.nbsinterfacepwd = karate.properties['dev.nbsinterfacepwd'];
    config.nbsdburl = karate.properties['dev.nbsdburl'];


  } else {
    karate.log('Unknown environment:', env);
    return;
  }

  return config;
}
