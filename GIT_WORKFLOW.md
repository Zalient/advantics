# Introduction
This document provides guidance on how our team should use Git and GitHub for version
control. Please follow these guidelines as best as possible and feel free to ask for help
when needed

# Branch Protection Rules and Best Practices
### 1. Main Branch Protection
- Direct pushing to `main` is forbidden and disabled
- **Force** pushing is also forbidden and disabled to protect the commit history
- Any changes to main branch must go through a pull request (PR)
### 2. CI/CD Pipeline Rules
- All code **must** pass CI/CD checks before merging into main
- If CI/CD checks **fail**, issues **must** be addressed before PR can be merged
### 3. Pull Requests and Code Reviews
- Pull request required before merging onto main
- Another team member **must** approve and code review the PR before merging
### 4. Branch Strategy
- We adopt a feature branch workflow. All development of features to bugfixes and what not should be done
  in its own **dedicated** branch instead of the `main` branch
- Steps are laid out in the section below

# Feature Branch Workflow
Adapted from [Atlassian](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow)

## The steps for feature-branching are as follows:
### 1. Clone the repository (if not done yet)
```
git clone <repo-url>
```
### 2. Get latest changes from main (pull latest changes)
```
git checkout main
git pull origin main
```
### 3. Create a new feature branch from main, name the branch appropriately (shown below)
```
git checkout -b feature-name
```
### 4. Work on your feature/changes
### 5. Stage and commit changes
```
git add .
git commit -m "feat: Add new upload button"
```
### 6. Push changes onto new/existing remote feature branch
```
git push origin feature-name
```
### 7. Create a pull request (PR) to merge feature branch into the `main`
### 8. Ensure all CI/CD checks pass and that a designated member is there to review the PR before merging
### 9. Once approval is received, merge the changes with the `main` branch

## How to Name Feature Branches
Adapted from [Medium](https://medium.com/@abhay.pixolo/naming-conventions-for-git-branches-a-cheatsheet-8549feca2534)

### General format: `<prefix>/<concise-description>`

### Branch Prefixes:

| Branch Prefixes | Definition                                |
|-----------------|-------------------------------------------|
| `feature`       | develop new features or functionality     |
| `bugfix`        | fixing a bug / issues                     |
| `hotfix`        | urgent fixes or patches                   |
| `release`       | prepare for new production release        |
| `docs`          | for writing/updating/fixing documentation |

### Examples:
- `feature/user-authentication`
- `bugfix/fix-header-styling`
- `docs/update-readme`

# Writing Commit Messages
Adapted from: [GitHub](https://gist.github.com/qoomon/5dfcdf8eec66a051ecd85625518cfd13)

### Following the structure of
```
<type>: <short description>

<optional detailed explanation>
```

| Types      | Definition                                                                               |
|------------|------------------------------------------------------------------------------------------|
| `feat`     | add or remove a new feature to the UI                                                    |
| `fix`      | fix a UI bug of a feature                                                                |
| `refactor` | restructure code, without changing program behaviour                                     |
| `perf`     | special refactor commits, that improve performance                                       |
| `style`    | changes do not affect the meaning (white-space, formatting, missing semi-colons, etc)    |
| `test`     | add missing tests or correcting existing tests                                           |
| `docs`     | affect documentation only                                                                |
| `build`    | affect build components like build tool, ci pipeline, dependencies, project version, etc |
| `ops`      | affect operational components like infrastructure, deployment, backup, recovery, etc     |
| `chore`    | miscellaneous commits e.g. modifying .gitignore                                          |

### Short Description
- a short message to explain what the commit was for or what it achieved

### Optional Detailed Explanation
- a longer more detailed description to explain what changes were made, what it was for, how it 
was done and additional context / information

### Examples
- `feat: Add user login page`
- `refactor: Rename SQL queries`
- `docs: Update GIT_WORKFLOW with PR instructions`

# CI/CD and Testing
### A CI/CD pipeline has been set up to trigger the workflow when new code is 
1. pushed onto the `main` branch
2. pull requested targeting the `main` branch

### CI/CD Workflow (stored in .github/workflows)
#### 1. Checkout Repository
   - Pulls the latest code from the repo info the CI/CD runner
#### 2. Set up JDK17
   - Installs JDK17 and caches maven dependencies for speeding up builds
#### 3. Clean Project
   - Deletes target folder and removes previous builds
#### 4. Compile Java Code 
   - Compiles the Java source code
#### 5. Run Unit and GUI Tests
   - Runs all JUnit and TestFX tests using `mvn test` and `xvfb -run` for GUI tests
#### 6. Upload Test Results
   - Saves test results to be accessed in GitHub Actions

### What to do when CI/CD workflow fails
#### 1. Check GitHub Actions logs to identify where the code failed
#### 2. Fix the issue locally 
#### 3. Re-push fixed code onto repository

### Test Libraries Used
- JUnit Test for logic testing
- TestFX Test for GUI testing

### Test file location:
Test files are located in the `src/test/com.university.grp20` folder

### How To Do Testing Locally
| Type                      | Command                         | Additional Info                                                                      |
|---------------------------|---------------------------------|--------------------------------------------------------------------------------------|
| Run all tests (Local)     | `mvn test`                      | Runs all tests in the test folder                                                    |
| Individual tests (Local)  | `mvn test -Dtest=ClassNameTest` | replace ClassNameTest with desired test from folder (e.g. `mvn test -Dtest=AppTest`) |


# Additional Help
Git commands https://www.atlassian.com/git/glossary#commands

How to use Git https://www.youtube.com/watch?v=mJ-qvsxPHpY&pp=ygUPZ2l0IGZvciBkdW1taWVz

Contact Team on Whatsapp or Discord