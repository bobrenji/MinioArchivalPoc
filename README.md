## MinIO Archival Poc
This is a POC to demonstrate how to handle archival of data in MinIO and how to restore it back.

### Prerequisites
- MinIO server running

### Steps to run the POC
1. Clone the repository
2. Install the MinIO server
```bash
   brew install minio/stable/minio
   ```
3. Start the MinIO server

```bash
   minio server /data
   ```
4. run the following command to create a bucket
```bash
   mc mb myminio/archive
   ```

5. Build application 
```gradle build```
6. Run the application
```gradle bootRun```
7. Access the application at http://localhost:8080

