# Reporting Telegram Bot
Telegram bot for managing working time. 

Domain termines: 
- Report: report of worked hours per day. List of timeRecords binded to day.
- TimeRecord: minimal unit of time that employee can add to report.

# Local development

1. Setup PostgresQL database:
Run command: 
```shell
docker-compose -f docker-compose-test.yml up -d
```
2. Configure and run Spring Boot Application:
Setup two env vars:
`bot.token=${BOT_TOKEN};bot.username=${BOT_USERNAME}`
Add VM Options: `-Dspring.profiles.active=test `

With these configurations - run SpringBoot main method.

# Technological stack
- SpringBoot as a skeleton framework
- Telegram-bot SpringBoot starter
- Spring Data starter
- Spring State Machine
- Spring Scheduler as a task manager
- PostgresQL databases
