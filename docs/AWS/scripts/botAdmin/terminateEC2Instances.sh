#!/bin/bash

echo 'Delete existing instances'
aws ec2 terminate-instances --instance-ids $(aws ec2 describe-instances --query 'Reservations[].Instances[].InstanceId' --filters "Name=tag:Name,Values=BizBotAdmin" --output text)

echo 'Delete existing ELB'
#aws elb delete-load-balancer --load-balancer-name BizBotAdminELB
