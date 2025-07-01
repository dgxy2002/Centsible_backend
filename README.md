# Centsible 💲💰

The world’s FIRST group-expense tracking application!

## 🎯 Problem
The rise of a strong consumer culture, heavily influenced by social media and targeted advertising, has significantly shaped spending habits. Constant exposure to marketing creates pressure to consume beyond one's means to project a desirable image online. This culture of instant gratification often leads to a lack of financial supervision, as individuals may prioritise short-term wants over long-term financial stability. Furthermore, with the proliferation of online scams, vulnerable groups such as the elderly and the youth would benefit from additional guidance and warnings. Without proper financial literacy or budgeting habits, this can result in poor financial management, with spending driven more by trends and peer influence than necessity or value. Over time, this environment may foster a lack of accountability for personal spending, normalising debt accumulation and impulsive purchases as part of everyday life.


## 💡 Features
* **Secure Authentication**

  * Login with username & password via Retrofit-backed API
  * Persists user ID, token, username & avatar URL in SharedPreferences
  * Friendly error messages on failure, plus seamless link to Signup

* **Unified Dashboard**

  * Checks for unread notifications on launch
  * Toggle between **Expenses**, **Income**, and **Budget** views
  * Personalized snapshot of your month’s spending and earnings

* **Expense Tracking**

  * Dynamic **pie chart** + RecyclerView of your monthly expenses by category
  * Log transactions with type, category, date, description, and optional receipt photo
  * Receipt capture ensures contextual, accurate records

* **Income Insights**

  * **Line graph** of daily income over the current month
  * List of all logged incomes, for quick drill-down

* **Budget Management**

  * Swipe through categories under “Overall Budget”
  * Tap any category to adjust its allocated amount on the fly

* **Smart Alerts**

  * RecyclerView of user-specific notifications, fetched in real time
  * Unread alerts trigger a toast reminder
  * Warnings when an expense > mean + 2×std dev (min 30 data points; default \$1000 demo)

* **Transaction History**

  * Full log of past expenses with amount, category, title & date
  * Reflect on previous decisions to plan ahead

* **Social Groups & Accountability**

  * Connect with family/friends via requests
  * View each other’s dashboards; send “nudges” to remind logging
  * Alerts for unusual spending in your network; swipe-to-remove connections

* **Gamified Motivation**

  * **Streaks**: Track consecutive days of logging, visually rewarded
  * **Leaderboard**: Rank users by quest-completion score for friendly competition

* **Customizable Profile**

  * Edit first/last name, birthday & bio
  * Upload or change profile picture for a personal touch


## 📸 Prototype
<table>
  <tr>
    <td><img src="https://github.com/dgxy2002/Centsible_backend/blob/master/readme_images/im1.jpg" width="240" alt="Prototype 1"/></td>
    <td><img src="https://github.com/dgxy2002/Centsible_backend/blob/master/readme_images/im2.jpg" width="240" alt="Prototype 2"/></td>
  </tr>
  <tr>
    <td><img src="https://github.com/dgxy2002/Centsible_backend/blob/master/readme_images/im3.jpg" width="240" alt="Prototype 3"/></td>
    <td><img src="https://github.com/dgxy2002/Centsible_backend/blob/master/readme_images/im4.jpg" width="240" alt="Prototype 4"/></td>
  </tr>
</table>

## ⚙️ Built With

- Android Studio
- Java
- MongoDB
- Docker

## 📃 Poster

![Poster](https://github.com/dgxy2002/Centsible_backend/blob/master/readme_images/image.png)
