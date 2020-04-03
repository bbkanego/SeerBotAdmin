#!/bin/bash

echo 'Build UI code'

cd $code_home/angular/SeerlogicsBotAdminUI/main-app
npm run prodBuild

echo 'copy angular code to S3'

#https://qiita.com/alokrawat050/items/56820afdb6968deec6a2
#aws s3api delete-objects --bucket gab.seersense.com --delete file://delete.json
aws s3 rm s3://gabadmin.seersense.com --recursive --profile bizBotAdmin
aws s3 cp $code_home/angular/SeerlogicsBotAdminUI/main-app/dist s3://gabadmin.seersense.com --profile bizBotAdmin --grants read=uri=http://acs.amazonaws.com/groups/global/AllUsers --recursive
