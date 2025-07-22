## Set Up Postman Environment Variables

Before sending requests, you need to configure the Postman environment with the required variables. This ensures that endpoints and authentication work smoothly across endpoints.

### Create an Environment:
1. In Postman, Click on the collection name **Data Ingestion API**, go to the **Variables** tab.
2. Add the following variables (make sure to set the values in Current Value):

| Key                  | Example Value             | Description                           |
|----------------------|---------------------------|---------------------------------------|
| `baseUrl`            | `https://api.myservice.com` | Base URL for the API                  |
| `keyCloakClientId`   | `my-client-id`            | Keycloak client ID for authentication |
| `keyCloakClientSecret` | `my-client-secret`      | Keycloak client secret                |
| `bearerToken`        | *(auto-generated)*        | JWT token (automatically populated)   |
| `messageUuid`        | *(auto-generated)*        | Used in status or dlt endpoints       |

3. Click **Save**.

> **Note:** If any request requires **query parameters**, be sure to fill those in using the **Params** tab of the request in Postman.

---

## Generate Authentication Token

To authenticate API requests, you must generate a **Bearer Token** using the Keycloak credentials.

### Instructions:
1. Open the **`Create JWT Token`** request in the collection.
2. Ensure the `clientid` and `clientsecret` headers are populated (using variables `{{keyCloakClientId}}` and `{{keyCloakClientSecret}}`).
3. Click **Send**.
4. If successful, the token is automatically saved to the `{{bearerToken}}` variable in the postman collection.

Once this token is set, you can make authenticated calls to any other endpoint in the postman collection.

---