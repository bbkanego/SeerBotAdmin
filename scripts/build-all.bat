cd C:\Bhushan\code\java\SeerlogicsBom
git pull origin develop

cd C:\Bhushan\code\java\EventGenie-Angular-Spring\eg-commons
git pull origin eventGenie-develop

cd C:\Bhushan\code\java\EventGenie-Angular-Spring\eg-spring
git pull origin eventGenie-develop

cd C:\Bhushan\code\java\SeerlogicsCloud
git pull origin develop

cd C:\Bhushan\code\java\SeerlogicsBotCommons
git pull origin develop

cd C:\Bhushan\code\java\SeerlogicsBotAdmin
git pull origin develop

cd C:\Bhushan\code\java\SeerLogicsSharedBot
git pull origin develop

call mvn -f C:\Bhushan\code\java\SeerlogicsBom\pom.xml  clean install
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn -f C:\Bhushan\code\java\EventGenie-Angular-Spring\eg-commons\pom.xml  clean install
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn -f C:\Bhushan\code\java\EventGenie-Angular-Spring\eg-spring\pom.xml  clean install
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn -f C:\Bhushan\code\java\SeerlogicsCloud\pom.xml  clean install
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn -f C:\Bhushan\code\java\SeerlogicsBotCommons\pom.xml  clean install
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn -f C:\Bhushan\code\java\SeerlogicsBotAdmin\pom.xml  clean install
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn -f C:\Bhushan\code\java\SeerLogicsSharedBot\pom.xml  clean install
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

cd C:\Bhushan\code\java\SeerlogicsBotAdmin\scripts