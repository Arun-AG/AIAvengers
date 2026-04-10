package com.adf.preOfferTest.miniApplicationPage3Test.EmployerLookUp;

import com.adf.Commands.commons.RedisClient;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EmployerLookupDetails {

    public static ExtentTest logger;

    public static void setLogger(ExtentTest logger) {
        if (logger == null) {
            throw new IllegalArgumentException("ExtentTest logger cannot be null");
        }
        EmployerLookupDetails.logger = logger;
    }

    private static HashMap<String, String> Empname = new HashMap<>();
    private static HashMap<String, Integer> NaicsCode = new HashMap<>();
    private static HashMap<String, String> LowAttributes = new HashMap<>();
    private static HashMap<String, String> MediumAttributes = new HashMap<>();
    private static HashMap<String, String> HighAttributes = new HashMap<>();
    private static Map<String, String> IncomeSource = new HashMap<>();
    private static HashMap<String, String> RecessionMapping = new HashMap<>();
    private static HashMap<String, String> EmpIndustry = new HashMap<>();
    private static final Logger log = LogManager.getLogger();
    
    static {
        try {
            loadRecessionValues();
            loadempnameValues();
            loadempnamewithrecession();
            loadNaicsCode();
            loadempIndustryValues();
        } catch (Exception e) {
            log.error("Failed to initialize static data in EmployerLookupDetails", e);
            throw new RuntimeException("Static initialization failed", e);
        }
    }

    @SuppressWarnings("serial")
    public static class RecessionException extends Exception {
        private final String exceptionValue;

        RecessionException(String exceptionName) {
            super(exceptionName);
            this.exceptionValue = exceptionName;
        }

        public String getExceptionValue() {
            return exceptionValue;
        }

        @Override
        public String toString() {
            return "RecessionException: " + exceptionValue;
        }
    }

    @SuppressWarnings("serial")
    public static class EmployerLookupException extends Exception {
        private final String operation;
        private final String errorCode;

        EmployerLookupException(String operation, String message, String errorCode, Throwable cause) {
            super(message, cause);
            this.operation = operation;
            this.errorCode = errorCode;
        }

        public String getOperation() {
            return operation;
        }

        public String getErrorCode() {
            return errorCode;
        }

        @Override
        public String toString() {
            return String.format("EmployerLookupException [Operation: %s, Code: %s, Message: %s]", 
                               operation, errorCode, getMessage());
        }
    }

    private static void loadRecessionValues() throws EmployerLookupException {
        try {
            // Manual exception throwing for validation scenarios
            
            // Check if RecessionMapping is null
            if (RecessionMapping == null) {
                throw new EmployerLookupException("loadRecessionValues", "RecessionMapping is null", "LOAD_001", null);
            }
            
            // Simulate configuration validation - throw exception if system property is set
            String simulateError = System.getProperty("simulate.recession.load.error");
            if ("true".equals(simulateError)) {
                throw new EmployerLookupException("loadRecessionValues", "Simulated recession loading error triggered", "LOAD_002", null);
            }
            
            // Validate attribute strings before processing
            String lowAttributes = "Oxford university/61222333/Educational Services,Stanfords university/61333444/Educational Services,Prince university/61444555/Educational Services";
            String mediumAttributes = null;
            String highAttributes = "Pride Industry/31222333/Manufacturing,Williams Industry/31555666/Manufacturing,LLL Industry/32333444/Manufacturing";
            
            // Manual validation - throw exception if attributes are empty
            if (lowAttributes == null || lowAttributes.trim().isEmpty()) {
                throw new EmployerLookupException("loadRecessionValues", "Low attributes string is null or empty", "LOAD_003", null);
            }
            
            if (mediumAttributes == null || mediumAttributes.trim().isEmpty()) {
                throw new EmployerLookupException("loadRecessionValues", "Medium attributes string is null or empty", "LOAD_004", null);
            }
            
            if (highAttributes == null || highAttributes.trim().isEmpty()) {
                throw new EmployerLookupException("loadRecessionValues", "High attributes string is null or empty", "LOAD_005", null);
            }
            
            // Simulate memory constraint check
            Runtime runtime = Runtime.getRuntime();
            long freeMemory = runtime.freeMemory();
            long totalMemory = runtime.totalMemory();
            long usedMemory = totalMemory - freeMemory;
            
            // Throw exception if memory usage is above 80%
            if (usedMemory > (totalMemory * 0.8)) {
                throw new EmployerLookupException("loadRecessionValues", "Insufficient memory to load recession values", "LOAD_006", null);
            }
            
            // Manual exception for duplicate key check
            if (RecessionMapping.containsKey("LOW")) {
                throw new EmployerLookupException("loadRecessionValues", "Duplicate LOW key found in RecessionMapping", "LOAD_007", null);
            }
            
            // Put values with manual exception handling for each operation
            try {
                RecessionMapping.put("LOW", "LOW");
            } catch (Exception e) {
                throw new EmployerLookupException("loadRecessionValues", "Failed to put LOW value in RecessionMapping", "LOAD_008", e);
            }
            
            try {
                RecessionMapping.put("MEDIUM", mediumAttributes);
            } catch (Exception e) {
                throw new EmployerLookupException("loadRecessionValues", "Failed to put MEDIUM value in RecessionMapping", "LOAD_009", e);
            }
            
            try {
                RecessionMapping.put("HIGH", highAttributes);
            } catch (Exception e) {
                throw new EmployerLookupException("loadRecessionValues", "Failed to put HIGH value in RecessionMapping", "LOAD_010", e);
            }
            
            // Final validation - check if all expected keys are present
            if (!RecessionMapping.containsKey("LOW") || !RecessionMapping.containsKey("MEDIUM") || !RecessionMapping.containsKey("HIGH")) {
                throw new EmployerLookupException("loadRecessionValues", "Not all required recession keys were loaded successfully", "LOAD_011", null);
            }
            
            // Simulate data integrity check
            int expectedSize = 3;
            if (RecessionMapping.size() != expectedSize) {
                throw new EmployerLookupException("loadRecessionValues", 
                    String.format("RecessionMapping size mismatch. Expected: %d, Actual: %d", expectedSize, RecessionMapping.size()), 
                    "LOAD_012", null);
            }
            
            log.info("Recession values loaded successfully with {} entries", RecessionMapping.size());
            
        } catch (EmployerLookupException e) {
            log.error("EmployerLookupException in loadRecessionValues: {}", e.toString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected exception in loadRecessionValues", e);
            throw new EmployerLookupException("loadRecessionValues", "Unexpected error loading recession values", "LOAD_013", e);
        }
    }

    private static void loadempnameValues() throws EmployerLookupException {
        try {
            Empname.put("LOW", "verizon1");
            Empname.put("MEDIUM", "Wholesaler");
            Empname.put("HIGH", "Industries");
            
            log.info("Employer name values loaded successfully");
        } catch (Exception e) {
            throw new EmployerLookupException("loadempnameValues", "Failed to load employer name values", "LOAD_002", e);
        }
    }

    private static void loadNaicsCode() throws EmployerLookupException {
        try {
            NaicsCode.put("LOW", 61222333);
            NaicsCode.put("MEDIUM", 42222333);
            NaicsCode.put("HIGH", 31222333);

            log.info("NAICS codes loaded successfully");
        } catch (Exception e) {
            throw new EmployerLookupException("loadNaicsCode", "Failed to load NAICS codes", "LOAD_003", e);
        }
    }

    private static void loadempnamewithrecession() throws EmployerLookupException {
        try {
            IncomeSource.put("Employment", "LOW,MEDIUM,HIGH");
            IncomeSource.put("Self-Employment", "HIGH");
            IncomeSource.put("Retired/Benefits", "MEDIUM");
            IncomeSource.put("Other", "MEDIUM");

            log.info("Income source mapping loaded successfully");
        } catch (Exception e) {
            throw new EmployerLookupException("loadempnamewithrecession", "Failed to load income source mapping", "LOAD_004", e);
        }
    }

    private static void loadempIndustryValues() throws EmployerLookupException {
        try {
            EmpIndustry.put("LOW", "Educational Services");
            EmpIndustry.put("MEDIUM", "Wholesale Trade");
            EmpIndustry.put("HIGH", "Manufacturing");
            
            log.info("Employer industry values loaded successfully");
        } catch (Exception e) {
            throw new EmployerLookupException("loadempIndustryValues", "Failed to load employer industry values", "LOAD_005", e);
        }
    }
    
    public static String returnAttributeValues(HashMap<String, Properties> prop) throws EmployerLookupException {
        try {
            validateInputProperties(prop);
            
            String recessionValue = prop.get("ExecProperty").getProperty("RecessionClassification");
            String attributes = prop.get("CustomerInfo").getProperty("empattributevalues");
            
            if (recessionValue == null || recessionValue.trim().isEmpty()) {
                throw new EmployerLookupException("returnAttributeValues", "RecessionClassification property is null or empty", "ATTR_001", null);
            }
            
            if (attributes == null || attributes.isEmpty()) {
                return RecessionMapping.getOrDefault(recessionValue, "LOW");
            } else {
                return "NO_RECORDS".equalsIgnoreCase(attributes) ? "" : attributes;
            }
        } catch (EmployerLookupException e) {
            throw e;
        } catch (Exception e) {
            throw new EmployerLookupException("returnAttributeValues", "Failed to return attribute values", "ATTR_002", e);
        }
    }

    public static Integer getNaicsCode(String recessionValue) throws EmployerLookupException {
        try {
            if (recessionValue == null || recessionValue.trim().isEmpty()) {
                log.warn("RecessionValue is null or empty, returning default NAICS code");
                return 61222333;
            }
            
            Integer code = NaicsCode.get(recessionValue);
            if (code == null) {
                log.warn("RecessionValue '{}' not found in NaicsCode mapping, returning default", recessionValue);
                return 61222333;
            }
            
            return code;
        } catch (Exception e) {
            throw new EmployerLookupException("getNaicsCode", "Failed to get NAICS code for value: " + recessionValue, "NAICS_001", e);
        }
    }

    public static String returnEmpName(String recessionValue) throws EmployerLookupException {
        try {
            if (recessionValue == null || recessionValue.trim().isEmpty()) {
                log.warn("RecessionValue is null or empty, cannot return employer name");
                return null;
            }
            
            String empName = Empname.get(recessionValue);
            if (empName == null) {
                log.warn("RecessionValue '{}' not found in Empname mapping", recessionValue);
            }
            
            return empName;
        } catch (Exception e) {
            throw new EmployerLookupException("returnEmpName", "Failed to return employer name for value: " + recessionValue, "EMP_001", e);
        }
    }

    public static void recessionCheck(HashMap<String, Properties> prop, String pathway) throws RecessionException, EmployerLookupException {
        try {
            validateInputProperties(prop);
            
            if (pathway == null || pathway.trim().isEmpty()) {
                throw new EmployerLookupException("recessionCheck", "Pathway cannot be null or empty", "REC_001", null);
            }
            
            if (!("RF".equalsIgnoreCase(pathway) || "RA".equalsIgnoreCase(pathway))) {
                log.info("Pathway '{}' is not RF or RA, skipping recession check", pathway);
                return;
            }
            
            String incomeSource = prop.get("CustomerInfo").getProperty("IncomeSource");
            String recessionClassification = prop.get("ExecProperty").getProperty("RecessionClassification");
            
            if (incomeSource == null || incomeSource.trim().isEmpty()) {
                throw new EmployerLookupException("recessionCheck", "IncomeSource property is null or empty", "REC_002", null);
            }
            
            if (recessionClassification == null || recessionClassification.trim().isEmpty()) {
                throw new EmployerLookupException("recessionCheck", "RecessionClassification property is null or empty", "REC_003", null);
            }
            
            String applicableValues = IncomeSource.get(incomeSource);
            if (applicableValues == null) {
                throw new EmployerLookupException("recessionCheck", "IncomeSource '" + incomeSource + "' not found in mapping", "REC_004", null);
            }
            
            if (applicableValues.contains(recessionClassification)) {
                if (logger != null) {
                    logger.log(Status.FAIL, "RecessionValue = " + recessionClassification + " Is Applicable For IncomeSource = " + incomeSource + "</pre>");
                }
                log.info("RecessionValue {} is applicable for IncomeSource {}", recessionClassification, incomeSource);
            } else {
                String message = String.format("RecessionValue = %s Is Not Applicable For IncomeSource = %s", recessionClassification, incomeSource);
                
                if (logger != null) {
                    logger.log(Status.FAIL, message + "</pre>");
                }
                
                RecessionException e = new RecessionException(message);
                log.error(message, e);
                throw e;
            }
            
        } catch (RecessionException e) {
            throw e;
        } catch (EmployerLookupException e) {
            throw e;
        } catch (Exception e) {
            throw new EmployerLookupException("recessionCheck", "Unexpected error during recession check", "REC_005", e);
        }
    }

    public static void setEmployerLookupBailout(HashMap<String, Properties> prop) throws EmployerLookupException {
        try {
            validateInputProperties(prop);
            
            String transactionID = prop.get("CustomerInfo").getProperty("TransactionID");
            String sessionID = prop.get("PageInfo").getProperty("SessionID");
            String pathway = prop.get("CustomerInfo").getProperty("Pathway");
            String environment = prop.get("ExecProperty").getProperty("Environment");
            String bailoutCounter = prop.get("ExecProperty").getProperty("BailoutCounter");
            
            if (transactionID == null || transactionID.trim().isEmpty()) {
                throw new EmployerLookupException("setEmployerLookupBailout", "TransactionID is null or empty", "BAIL_001", null);
            }
            
            if (sessionID == null || sessionID.trim().isEmpty()) {
                throw new EmployerLookupException("setEmployerLookupBailout", "SessionID is null or empty", "BAIL_002", null);
            }
            
            if (environment == null || environment.trim().isEmpty()) {
                throw new EmployerLookupException("setEmployerLookupBailout", "Environment is null or empty", "BAIL_003", null);
            }
            
            if (bailoutCounter == null || bailoutCounter.trim().isEmpty()) {
                throw new EmployerLookupException("setEmployerLookupBailout", "BailoutCounter is null or empty", "BAIL_004", null);
            }
            
            String incomeSource = "0";
            switch (pathway) {
                case "RF":
                case "PS":
                    incomeSource = "0";
                    break;
                default:
                    log.warn("Unknown pathway '{}', using default income source", pathway);
                    incomeSource = "0";
            }
            
            String key = sessionID + "_" + transactionID + "_" + incomeSource;
            String djangoCacheVersion = ":1:";
            String bailOutCounterKey = djangoCacheVersion + key;
            String initializerKey = djangoCacheVersion + "el_unique_id_" + key;
            
            String redisURL = prop.get("APIDataProperty").getProperty("RedisConnectionUI_" + environment);
            if (redisURL == null || redisURL.trim().isEmpty()) {
                throw new EmployerLookupException("setEmployerLookupBailout", "Redis URL not found for environment: " + environment, "BAIL_005", null);
            }
            
            log.info("Connecting to Redis at URL: {}", redisURL);
            
            try {
                RedisClient.getRedisClient(environment).setValue(bailOutCounterKey, bailoutCounter);
                log.info("Bailout Cache Key -> {} value -> {}", key, bailoutCounter);
            } catch (Exception redisException) {
                throw new EmployerLookupException("setEmployerLookupBailout", "Failed to set Redis value", "BAIL_006", redisException);
            }
            
        } catch (EmployerLookupException e) {
            log.error("EmployerLookupException in setEmployerLookupBailout", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected exception in setEmployerLookupBailout", e);
            throw new EmployerLookupException("setEmployerLookupBailout", "Unexpected error setting employer lookup bailout", "BAIL_007", e);
        }
    }

    public static String getEmployerIndustry(String recessionValue) throws EmployerLookupException {
        try {
            if (recessionValue == null || recessionValue.trim().isEmpty()) {
                log.warn("RecessionValue is null or empty, cannot return employer industry");
                return null;
            }
            
            String industry = EmpIndustry.get(recessionValue);
            if (industry == null) {
                log.warn("RecessionValue '{}' not found in EmpIndustry mapping", recessionValue);
            }
            
            return industry;
        } catch (Exception e) {
            throw new EmployerLookupException("getEmployerIndustry", "Failed to get employer industry for value: " + recessionValue, "IND_001", e);
        }
    }
    
    private static void validateInputProperties(HashMap<String, Properties> prop) throws EmployerLookupException {
        if (prop == null) {
            throw new EmployerLookupException("validateInputProperties", "Properties map cannot be null", "VAL_001", null);
        }
        
        if (!prop.containsKey("ExecProperty") || prop.get("ExecProperty") == null) {
            throw new EmployerLookupException("validateInputProperties", "ExecProperty map is missing or null", "VAL_002", null);
        }
        
        if (!prop.containsKey("CustomerInfo") || prop.get("CustomerInfo") == null) {
            throw new EmployerLookupException("validateInputProperties", "CustomerInfo map is missing or null", "VAL_003", null);
        }
        
        if (!prop.containsKey("PageInfo") || prop.get("PageInfo") == null) {
            throw new EmployerLookupException("validateInputProperties", "PageInfo map is missing or null", "VAL_004", null);
        }
        
        if (!prop.containsKey("APIDataProperty") || prop.get("APIDataProperty") == null) {
            throw new EmployerLookupException("validateInputProperties", "APIDataProperty map is missing or null", "VAL_005", null);
        }
    }
}
