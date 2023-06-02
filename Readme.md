## Steps to run this code easily

1. Fork this repo, create a github codespace for this repository on master branch, it may take couple of mins for few mins for the first time.
2. Upon, navigating to VS code editor, open the TERMINAL tab in the bottom panel.
   1. Compile the app by running `mvn clean install` (This may take couple of mins).
   2. Bring both mysql and app live with `docker compose up` (This may take couple of mins, tries to bring up mysql post that boots up app wait till you see log with message "Picked storage.path as ./data").
3. Navigate to PORTS section in the bottom panel where you can find the local address for the server running on port 8080, do a ctrl or cmd click on it, post opening of the link in new browser window navigate to swagger-ui by appending `/swagger-ui.html` to the url
4. Enjoy testing the api's from the swagger-ui.

## A flow to Test

1. Create a user by providing username/password using the Sign Up opearation, (remember your username and password pair).
2. Do a Login to get the JWT for being authenticated to perform the next operations, enter your username and password pair and copy the response.
3. Find the Authorize button on top, beside the server url. Click on it, and paste the JWT you copied in the previous step and click Authorize, next click Close.
4. Let us start testing Photos App by uploading a photo, Navigate to Upload a file operation, keep a image to upload handy. Now you can upload file in the request body's file field and you can optionally save with a different name by providing a different name at name field above the Request body section, upon successful upload you can get a PhotoId, you upload as images as you wish.
5. Now we will try to get our photos name list, by navigating to Get all photos section in Swagger UI, hit get Execute button to fetch the list of images you uploaded till now, remember a photo's id, so that you can download in the next step.
6. How about downloaing one ? So navigate to Download Photo, now provide the photoId of any photo uploaded previously.
7.
