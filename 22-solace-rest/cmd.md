

curl -X POST https://mr-e3zjti4y6ai.messaging.solace.cloud:9443/Topic/joepoc -d "Hello World REST" -H "content-type: text" -H "Solace-delivery-mode: direct" --user solace-cloud-client:dnuqffh8dh8dgt4flludfrot26

curl -X POST https://mr-e3zjti4y6ai.messaging.solace.cloud:9443/Topic/joepoc -d "Hello World REST" -H "content-type: text" -H "Solace-delivery-mode: persistent" --user solace-cloud-client:dnuqffh8dh8dgt4flludfrot26

curl -X POST https://mr-e3zjti4y6ai.messaging.solace.cloud:9443/Queue/joequeue -d "Hello World REST" -H "content-type: text" -H "Solace-delivery-mode: persistent" --user solace-cloud-client:dnuqffh8dh8dgt4flludfrot26
