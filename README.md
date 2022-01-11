# Reporting Telegram Bot

## Deployment
Deployment process as easy as possible:
Required software:
- terminal for running bash scripts
- docker
- docker-compose

to deploy application, switch to needed branch and run bash script:

$ bash start.sh ${bot_username} ${bot_token}

That's all.

# Local development
For local development and testing, use `docker-compose-test.yml`. 
Run command: 
```shell
docker-compose -f docker-compose-test.yml up -d
```
Next step, is to run SpringBoot app with configured **Edit Configuration** in which two env vars are provided: 

`bot.token=${BOT_TOKEN};bot.username=${BOT_USERNAME}`

And add VM Options: 

`-Dspring.profiles.active=test `

 With these configurations - run SpringBoot main method.

# Technological stack
- SpringBoot as a skeleton framework
- Spring Scheduler as a task manager
- MySQL database as a database for saving user and subscription info
- Telegram-bot SpringBoot starter
- Spring Data starter
- Unirest - lib for working with REST calls
