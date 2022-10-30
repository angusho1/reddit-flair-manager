# Reddit Flair Manager

A simple Android app for managing a user's flairs

## Demo
https://user-images.githubusercontent.com/57027339/198865759-d8ee5057-da7a-4ca3-960a-a4ac473ec1ce.mp4

## Getting Started

1. [Create a Reddit application](https://www.reddit.com/prefs/apps) to enable OAuth login for the user
- Selecting "installed app" doesn't work as it uses the implicit flow for access token retrieval, and currently Reddit's APIs do not work at all with tokens issues through this flow (issue discussion [here](https://www.reddit.com/r/redditdev/comments/qgr0np/installed_app_with_access_token_always_receive/)). For personal use, you can opt for "script" as the app type as it will allow Android custom deep links for the OAuth redirect URI. Otherwise, "web app" requires an ```https``` redirect.
- If using a custom Android deeplink, set your redirect uri to ```<SCHEME>://<HOST>```, where ```SCHEME``` and ```HOST``` are values of your own choosing. ```SCHEME``` should be unique to prevent clashing with other installed apps.

2. Add an untracked resource file, ```app/src/main/res/values/secrets.xml```. The file should contain the following:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="reddit_app_id">YOUR_APP_ID</string>
    <string name="reddit_app_secret">YOUR_APP_SECRET</string>
    <string name="app_scheme">SCHEME</string>
    <string name="app_host">HOST</string>
</resources>
```

