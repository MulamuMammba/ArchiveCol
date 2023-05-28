package com.example.archivecol.model

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.archivecol.R

class Achievements {
    object AchievementConstants {
        const val ACHIEVEMENT_STARTER = "Starter"
        const val ACHIEVEMENT_COLLECTOR = "Collector"
        const val ACHIEVEMENT_PACKRAT = "Packrat"

        const val THRESHOLD_STARTER = 1
        const val THRESHOLD_COLLECTOR = 3
        const val THRESHOLD_PACKRAT = 10
    }

    companion object {
        fun itemCreated(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("Achievements", Context.MODE_PRIVATE)

            var count = sharedPreferences.getInt("total_items_created", 0)
            count += 1
            sharedPreferences.edit()
                .putInt("total_items_created", count)
                .apply()

            val achievementsMap = mapOf(
                AchievementConstants.ACHIEVEMENT_STARTER to AchievementConstants.THRESHOLD_STARTER,
                AchievementConstants.ACHIEVEMENT_COLLECTOR to AchievementConstants.THRESHOLD_COLLECTOR,
                AchievementConstants.ACHIEVEMENT_PACKRAT to AchievementConstants.THRESHOLD_PACKRAT
            )

            for ((achievement, threshold) in achievementsMap) {
                var progress = sharedPreferences.getInt(achievement, 0)
                progress++
                sharedPreferences.edit()
                    .putInt(achievement, progress)
                    .apply()

                if (progress == threshold) {
                    showAchievement(context, achievement)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun showAchievement(context: Context, rank: String) {
            val dialogBuilder = AlertDialog.Builder(context)
            val layoutInflater = LayoutInflater.from(context)
            val dialogView = layoutInflater.inflate(R.layout.achievement_dialog, null)

            val trophyImageView = dialogView.findViewById<ImageView>(R.id.trophyImageView)
            val rankTextView = dialogView.findViewById<TextView>(R.id.rankTextView)
            val congratsTextView = dialogView.findViewById<TextView>(R.id.congratsTextView)

            when (rank) {
                "Starter" -> trophyImageView.setImageResource(R.drawable.medal_bronze)
                "Collector" -> trophyImageView.setImageResource(R.drawable.medal_silver)
                "Packrat" -> trophyImageView.setImageResource(R.drawable.medal_gold)
            }
            rankTextView.text = "Achieved Rank: $rank"
            congratsTextView.text = "Congratulations on reaching $rank!"

            dialogBuilder.setView(dialogView)
            val dialog = dialogBuilder.create()
            dialog.show()
        }
    }
}
