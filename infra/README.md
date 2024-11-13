<!--
SPDX-FileCopyrightText: 2024 PNED G.I.E.

SPDX-License-Identifier: CC-BY-4.0
-->
## To Run the application locally 

* Create a new file named .env.secrets in infra/config by copying .env.secrets.template and updating the secret values
* To build and start the docker image using your local changes Run the script
    
  ```bash
  ./start.sh
    ```
* To stop the application run the below script
  
  ```bash
  ./stop.sh
  ```