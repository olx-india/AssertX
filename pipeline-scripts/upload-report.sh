if [ $# -eq 0 ]
then
  exit 1
fi

export AWS_DEFAULT_REGION=${REGION:-"us-east-1"}

S3_BUCKET=${S3_BUCKET:-"reports"}

# Get target folder path from args, default path is target/
if [ -n "$2" ]
then
  TARGET_PATH=${2}
else
  TARGET_PATH="target"
fi

aws s3 cp $TARGET_PATH/site/jacoco-it s3://${S3_BUCKET}/${1}/${CI_PIPELINE_ID}/assertx/coverage-report --recursive --acl public-read
aws s3 cp $TARGET_PATH/cucumber-html-reports s3://${S3_BUCKET}/${1}/${CI_PIPELINE_ID}/assertx/cucumber-report --recursive --acl public-read