#!/bin/bash
# LocalStack init script copied from localstack.dataDir; creates a bucket the suite expects to exist.
awslocal s3 mb s3://seeded-bucket
