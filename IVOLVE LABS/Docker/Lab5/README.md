# Lab 5: Docker Image Vulnerability Scanning with Trivy

## Objective

Build a Docker image for a Node.js application, scan it for vulnerabilities using Trivy, filter HIGH and CRITICAL findings, and generate HTML reports.

## Application

Source code: [TrivyImage](https://github.com/Ibrahim-Adel15/TrivyImage)

The project includes a Dockerfile, `app.js`, and `package.json`. The Dockerfile uses Node.js 18, installs the application dependencies, and exposes port 3000.

## 1. Build the Docker image

Run these commands from the directory containing the Dockerfile and application files:

```bash
docker build -t trivy-lab:v1 .
docker images trivy-lab:v1
### 2. Install Trivy on Ubuntu
sudo apt-get update
sudo apt-get install wget gnupg -y

wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key \
  | gpg --dearmor \
  | sudo tee /usr/share/keyrings/trivy.gpg > /dev/null

echo "deb [signed-by=/usr/share/keyrings/trivy.gpg] https://aquasecurity.github.io/trivy-repo/deb generic main" \
  | sudo tee -a /etc/apt/sources.list.d/trivy.list

sudo apt-get update
sudo apt-get install trivy -y
trivy --version
### 3. Run a full vulnerability scan
Create a directory for the scan results, then scan the image:
mkdir -p results

trivy image --format table \
  --output results/trivy-full-scan.txt \
  trivy-lab:v1
### 4. Scan for HIGH and CRITICAL vulnerabilities
trivy image --severity HIGH,CRITICAL trivy-lab:v1
Review each finding's severity, vulnerability ID, affected package, installed version, and fixed version if one is available.
### 5. Generate HTML reports
Download Trivy's HTML template:
wget -O results/html.tpl \
  https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/html.tpl
Generate a full report:
trivy image --format template --template "@results/html.tpl" \
  --output results/trivy-report.html \
  trivy-lab:v1
Generate a report containing only HIGH and CRITICAL vulnerabilities:
trivy image --severity HIGH,CRITICAL \
  --format template --template "@results/html.tpl" \
  --output results/trivy-high-critical.html \
  trivy-lab:v1
Open the HTML reports in a browser to review the scan results.
### Results
The results/ directory contains:
- trivy-full-scan.txt — full scan in table format.
- trivy-report.html — full scan in HTML format.
- trivy-high-critical.html — HIGH and CRITICAL findings in HTML format.
- html.tpl — template used to generate the HTML reports.
Scan results depend on the image contents and the vulnerability database available when the scan runs.
Bonus: Vulnerability remediation
Review the HIGH and CRITICAL findings and check whether fixes are available. Update the base image or application dependencies where appropriate, then rebuild and rescan:
docker build -t ivolve-app:v2 .
trivy image --severity HIGH,CRITICAL ivolve-app:v2
Save the new scan report. Some vulnerabilities may remain when no fix is available.
### References
- TrivyImage application repository
- Trivy installation documentation
- Trivy reporting documentation <img width="1512" height="805" alt="image" src="https://github.com/user-attachments/assets/72e0fb98-437c-494f-ac4c-fda192c0f109" />
