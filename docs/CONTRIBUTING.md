
# Contribution guidelines

## Basic

- If there is not one open already, open an issue in [GitHub issues](https://github.com/paypal/butterfly/issues)
- Fork this repo
- Checkout `develop` branch
- Apply your changes
  - Make sure all projects build and all unit tests pass
  - Make sure code coverage doesn't drop (add extra unit tests if necessary)
  - If fixing a bug, make sure you add an unit or integration test to expose the issue
  - If adding a new feature, make sure you add an unit or integration test to test the feature
  - If adding a new feature, add end user documentation as well
  - Add comments to the code explaining your changes if necessary
- Create a pull request to the correct development branch (mention the issue id in the PR name)

## Code style

Make sure to follow the code style of the existing code. That means for example four spaces for indentation.

## Commit messages

When committing, make sure the commit message is describing what is changed and why. See the example below.

    short description #issue_id
    A more detailed description

## Code review process

Code review rules when addressing review comments:

1. If possible, provide a single new commit addressing all pending comments per code review iteration.
1. Don't squash the new commit addressing review comments. This way the code review process keeps the history of the changes, which helps the discussion. GitHub will squash everything into a single commit when the PR is merged.
1. After addressing a comment, don't mark it as resolved. That is up to the reviewer to do. This helps to keep track of what is pending what is not. Instead, please add a comment like "done" once your commit addressing the comment is pushed.
1. Feel free to disagree with any comment and share your thoughts. New ideas are always welcome :-).

## Communication

Please send an email to fabiocarvalho777@gmail.com.

## More information

Read more about best practices in [this github guide](https://guides.github.com/activities/contributing-to-open-source/).
