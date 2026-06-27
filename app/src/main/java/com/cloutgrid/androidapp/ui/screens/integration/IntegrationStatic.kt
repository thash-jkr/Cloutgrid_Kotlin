package com.cloutgrid.androidapp.ui.screens.integration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InstagramConstants(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Connecting your Instagram unlocks analytics that help you stand out to businesses 🙋🏻‍♂️. This transparency builds trust, boosts your credibility, and increases your chances of securing collaborations 🤝.",
            fontSize = 13.sp, // Matches iOS .footnote
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        InfoSection(title = "What you’ll get once connected:") {
            BulletPoint("Verified display of your follower count, followings, and media count.")
            BulletPoint("Insights into your reach, profile views, and audience engagement shown on your Cloutgrid profile.")
            BulletPoint("Access to detailed media insights (likes, comments, impressions, video views) that brands care about.")
            BulletPoint("A stronger, more credible profile that businesses can evaluate at a glance.")
        }

        InfoSection(title = "What you need before connecting:") {
            BulletPoint("Your Instagram must be a Creator or Business account (personal accounts cannot connect).")
            BulletPoint("Your Instagram account must be linked to a Facebook Page (Meta requires this link for insights).")
            BulletPoint("You’ll log in with your Facebook credentials to complete the connection.")
        }

        InfoSection(title = "How to connect:") {
            BulletPoint("Make sure your Instagram is switched to a Creator or Business account (you can change this in Instagram Settings → Account).")
            BulletPoint("Ensure your Instagram is linked to a Facebook Page you manage.")
            BulletPoint("Click “Connect Instagram” above and log in with Facebook.")
            BulletPoint("Grant the requested permissions (needed to pull your analytics securely).")
        }
    }
}

@Composable
fun YoutubeConstants(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Connecting your YouTube channel unlocks verified metrics that demonstrate your influence 🚀. Providing real-time data builds professional credibility and makes it easier for brands to partner with you 🤝.",
            fontSize = 13.sp, // Matches iOS .footnote
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        InfoSection(title = "What you’ll get once connected:") {
            BulletPoint("Verified subscriber count and lifetime video views displayed on your profile.")
            BulletPoint("Real-time data on your average view duration, watch time, and click-through rates.")
            BulletPoint("Audience demographics including age, gender, and top geographic locations.")
            BulletPoint("Performance trends for your latest uploads and most popular content.")
        }

        InfoSection(title = "What you need before connecting:") {
            BulletPoint("A YouTube channel with active content (public or unlisted videos).")
            BulletPoint("The Google Account credentials associated with your YouTube channel.")
            BulletPoint("Approval for Cloutgrid to view your YouTube Analytics reports via Google’s secure login.")
        }

        InfoSection(title = "How to connect:") {
            BulletPoint("Ensure you are logged into the Google Account that manages your YouTube channel.")
            BulletPoint("Click “Connect YouTube” above to open the secure Google Sign-In prompt.")
            BulletPoint("Select the specific channel you wish to link to Cloutgrid.")
            BulletPoint("Grant the requested permissions so we can securely display your analytics to potential partners.")
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            fontSize = 17.sp, // Matches iOS .headline
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(
            modifier = Modifier.padding(start = 5.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "•",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = text,
            fontSize = 15.sp, // Matches iOS .subheadline
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}