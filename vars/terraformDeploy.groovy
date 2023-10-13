import org.techiescamp.GlobalConfig

def init(Map params) {
   def projectDirectory = params.projectDirectory
   def tfstateFile = params.tfstateFile
   def variableFile = params.variableFile

   dir(projectDirectory) {
      def terraformInitCommand = """
        terraform init \\
          -backend-config="key=dev/${tfstateFile}" \\
          -backend-config="bucket=${GlobalConfig.backendS3Bucket}" \\
          -backend-config="region=${GlobalConfig.awsRegion}" \\
          -backend-config="dynamodb_table=${GlobalConfig.backendDynamodbTable}" \\
          -var-file=../../vars/infra/stage/${variableFile}
      """

      sh terraformInitCommand
   }
}

def validate(Map params) {
   def projectDirectory = params.projectDirectory

   dir(projectDirectory) {
      def terraformValidateCommand = "terraform validate"
      sh terraformValidateCommand 
   }   
}

def plan(Map params) {
   def projectDirectory = params.projectDirectory
   def variableFile = params.variableFile
   def planFile = params.planFile
   def variableMap = params.variables

   dir(projectDirectory) {
      def variableOptions = ""
      variableMap.each { key, value ->
         if (value != null) {
            variableOptions += "-var=$key=$value "
         }
      }

      def terraformPlanCommand = "terraform plan $variableOptions -var-file=../../vars/$variableFile"
      sh terraformPlanCommand
   }
}

def show(Map params) {
   def projectDirectory = params.projectDirectory
   def planFile = params.planFile
   def redirectPlanFile = params.redirectPlanFile

   dir(projectDirectory) {
      def terraformShowCommand = "terraform show -json $planFile | jq > $redirectPlanFile"
      sh terraformShowCommand
   }   
}

def apply(Map params) {
   def projectDirectory = params.projectDirectory
   def variableFile = params.variableFile
   def amiId = params.amiId

   dir(projectDirectory) {
      def amiIdOption = amiId != null ? "-var=ami_id=$amiId" : ""
      def terraformApplyCommand = "terraform apply $amiIdOption -var-file=../../vars/$variableFile --auto-approve"
      sh terraformApplyCommand 
   }   
}

def destroy(Map params) {
   def projectDirectory = params.projectDirectory
   def variableFile = params.variableFile
   def amiId = params.amiId

   dir(projectDirectory) {
      def amiIdOption = amiId != null ? "-var=ami_id=$amiId" : ""
      def terraformDestroyCommand = "terraform destroy $amiIdOption -var-file=../../vars/$variableFile --auto-approve"
      sh terraformDestroyCommand 
   }   
}

