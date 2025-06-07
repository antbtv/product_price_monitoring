#!/bin/bash
mvn clean install -DskipTests && docker-compose down && docker-compose up -d --build