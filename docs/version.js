const fs = require('fs');
const path = require('path');

const gradlePropertiesPath = path.resolve(__dirname, '../gradle.properties');
// Define the path to the docs file
const filePath = path.resolve(__dirname, 'docs/2-getting-started.md');

// Function to get the version from the gradle.properties file
function getVersionFromGradleProperties(filePath) {
    try {
        // Read the contents of the gradle.properties file
        const data = fs.readFileSync(filePath, 'utf8');

        // Split the data by lines and find the line containing 'systemProp.version'
        const lines = data.split('\n');
        const versionLine = lines.find(line => line.startsWith('systemProp.version='));

        // If the line is found, extract the version value
        if (versionLine) {
            const version = versionLine.split('=')[1].trim();
            return version;
        } else {
            throw new Error('Version property not found in gradle.properties');
        }
    } catch (err) {
        console.error('Error reading gradle.properties:', err.message);
        return null;
    }
}

// Function to replace the version in the file
function replaceVersion(filePath, newVersion) {
    try {
        // Read the contents of the file
        const data = fs.readFileSync(filePath, 'utf8');

        // Define the regular expression to match the version pattern
        const versionRegex = /version '\d+\.\d+\.\d+'/g;

        // Replace the old version with the new version
        const updatedData = data.replace(versionRegex, `version '${newVersion}'`)

        // Write the updated content back to the file
        fs.writeFileSync(filePath, updatedData, 'utf8');

        console.log('Version updated successfully.');
    } catch (err) {
        console.error('Error updating version:', err.message);
    }
}
// Get the version and print it
let version = process.env.RELEASE_VERSION;
if(process.env.RELEASE_VERSION == undefined) {
    version = getVersionFromGradleProperties(gradlePropertiesPath);
}
console.log(`Resolved version: ${version}`);
replaceVersion(filePath, version);


