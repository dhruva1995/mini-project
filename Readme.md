# Steps to run this code easily

1. Fork this repo, create a github codespace for this repository on master branch, it may take couple of mins for few mins for the first time.
2. Upon, navigating to VS code editor,
   1. Compile the app by running `mvn clean install`
   2. Bring both mysql and app live with `docker compose up`
3. Navigate to PORTS section in the bottom panel where you can find the local address for the server running on port 8080, do a ctrl or cmd click on it, post opening of the link in new browser window navigate to swagger-ui by appending `/swagger-ui.html` to the url
4. Enjoy testing the api's from the swagger-ui.
