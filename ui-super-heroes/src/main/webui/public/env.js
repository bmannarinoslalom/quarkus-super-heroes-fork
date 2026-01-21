// Configuration for standalone React development
// This file is loaded by index.html and sets up the API configuration
// When running with Quarkus, this file is served dynamically by EnvResource.java
// When running standalone with npm, this static file is used instead

window.APP_CONFIG = {
  // Point to the rest-fights backend running via docker-compose
  API_BASE_URL: "http://localhost:8082",
  CALCULATE_API_BASE_URL: false
};
