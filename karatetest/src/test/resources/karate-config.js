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
    config.wrongapiurl = karate.properties['test.wrongapiurl'];
    config.nbsurl = karate.properties['test.nbsurl'];


    karate.log('config object after setting test properties:', JSON.stringify(config));
    karate.log('Setting test properties:');
        karate.log('apiurl:', config.apiurl);
        karate.log('wrongapiurl:', config.wrongapiurl);
        karate.log('username:', config.username);
        karate.log('password:', config.password);
        karate.log('url:', config.url);
        karate.log('driverClassName:', config.driverClassName);
        karate.log('bootstrapServers:', config.bootstrapServers);
        karate.log('groupId:', config.groupId);

        
  } else if (env == 'dev') {
    config.apiurl = karate.properties['dev.apiurl'];
    config.username = karate.properties['dev.username'];
    config.password = karate.properties['dev.password'];
    config.url = karate.properties['dev.url'];
    config.driverClassName = karate.properties['dev.driverClassName'];
    config.bootstrapServers = karate.properties['dev.bootstrapServers'];
    config.groupId = karate.properties['dev.groupId'];
    config.wrongapiurl = karate.properties['dev.wrongapiurl'];

    karate.log('config object after setting dev properties:', JSON.stringify(config));
  } else {
    karate.log('Unknown environment:', env);
    return;
  }

  return config;
}
