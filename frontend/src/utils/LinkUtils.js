export function taskLinkString(taskId) {
    return `https://app.asana.com/0/0/${taskId}`
}

export function projectLinkString(projectId) {
    return `https://app.asana.com/0/${projectId}`
}

export function emailLinkString(email) {
    return `mailto:${email}`
}

export function driveSearchString(jobTitle) {
    return `https://drive.google.com/drive/u/0/search?q=type:folder%20title:"${jobTitle}"`;
}
