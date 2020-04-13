#!/bin/bash

botAdminUI=$code_home/angular/SeerlogicsBotAdminUI

echo 'Building bootstrap chat client JS'
cd $botAdminUI/chat-client-bootstrap
npm run prepack
cp $botAdminUI/chat-client-bootstrap/public/javascript/seer-chat-bootstrap.min.js $botAdminUI/main-app/src/assets/bootstrap/seer-chat-bootstrap.min.js

echo 'Build UI code'

cd $botAdminUI/main-app
npm run prodBuild

echo 'copy angular code to S3'

#https://qiita.com/alokrawat050/items/56820afdb6968deec6a2
#aws s3api delete-objects --bucket gab.seersense.com --delete file://delete.json
aws s3 rm s3://gabstatic.seersense.com --recursive --profile bizBotAdmin
aws s3 cp $botAdminUI/main-app/dist s3://gabstatic.seersense.com --profile bizBotAdmin --grants read=uri=http://acs.amazonaws.com/groups/global/AllUsers --recursive
