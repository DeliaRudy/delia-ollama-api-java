# Deployment Instructions

This document provides instructions on how to deploy the application to Google Cloud Run.

## Prerequisites

1.  A Google Cloud Platform (GCP) project.
2.  The `gcloud` command-line tool installed and configured.
3.  Docker installed on your local machine.
4.  A `google-analytics-credentials.json` file with the service account credentials.

## Steps

1.  **Build the application:**

    ```bash
    mvn -f pom.xml clean install
    ```

2.  **Build the Docker image:**

    ```bash
    docker build -t gcr.io/YOUR_PROJECT_ID/persona-generator .
    ```

    Replace `YOUR_PROJECT_ID` with your GCP project ID.

3.  **Push the Docker image to Google Container Registry:**

    ```bash
    docker push gcr.io/YOUR_PROJECT_ID/persona-generator
    ```

4.  **Enable APIs:**

    Enable the Vertex AI API and the Google Analytics Data API for your project. You can do this from the Google Cloud Console.

5.  **Deploy to Cloud Run:**

    ```bash
    gcloud run deploy persona-generator \
        --image gcr.io/YOUR_PROJECT_ID/persona-generator \
        --platform managed \
        --region us-central1 \
        --allow-unauthenticated \
        --set-env-vars="GA_PROPERTY_ID=YOUR_GA_PROPERTY_ID,VERTEX_AI_PROJECT_ID=YOUR_PROJECT_ID,VERTEX_AI_LOCATION=us-central1,VERTEX_AI_MODEL_ID=gemini-1.0-pro" \
        --set-secrets="GOOGLE_APPLICATION_CREDENTIALS=google-analytics-credentials:latest"
    ```

    Replace `YOUR_PROJECT_ID` with your GCP project ID and `YOUR_GA_PROPERTY_ID` with your Google Analytics Property ID.

    **Note on Permissions:**
    You will need to grant the Cloud Run service account the "Vertex AI User" role in the IAM settings of your GCP project.

    **Note on Secrets:**
    The `--set-secrets` flag assumes you have created a secret in Google Secret Manager named `google-analytics-credentials` with the content of your `google-analytics-credentials.json` file.

    To create the secret, you can use the following command:

    ```bash
    gcloud secrets create google-analytics-credentials --data-file=google-analytics-credentials.json
    ```

    You will also need to grant the Cloud Run service account access to this secret.

5.  **Access the application:**

    Once the deployment is complete, `gcloud` will provide you with a URL to access your application.
