package com.example.archivecol.ui

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archivecol.R
import com.example.archivecol.model.Achievements

class AchievementsScreen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AchievementAdapter
    private lateinit var achievementsList: List<Achievement>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievements_screen)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load achievements and item count
        achievementsList = loadAchievements()
        findViewById<TextView>(R.id.itemsCreatedTextView).text =
            "Items Created: ${loadTotalItemsCreated()}"

        // Set up adapter
        adapter = AchievementAdapter(achievementsList)
        recyclerView.adapter = adapter
    }

    private fun loadAchievements(): List<Achievement> {
        val sharedPreferences = getSharedPreferences("Achievements", Context.MODE_PRIVATE)
        val starterProgress =
            sharedPreferences.getInt(Achievements.AchievementConstants.ACHIEVEMENT_STARTER, 0)
        val collectorProgress =
            sharedPreferences.getInt(Achievements.AchievementConstants.ACHIEVEMENT_COLLECTOR, 0)
        val packratProgress =
            sharedPreferences.getInt(Achievements.AchievementConstants.ACHIEVEMENT_PACKRAT, 0)

        val achievements = mutableListOf<Achievement>()
        achievements.add(
            Achievement(
                Achievements.AchievementConstants.ACHIEVEMENT_STARTER,
                starterProgress
            )
        )
        achievements.add(
            Achievement(
                Achievements.AchievementConstants.ACHIEVEMENT_COLLECTOR,
                collectorProgress
            )
        )
        achievements.add(
            Achievement(
                Achievements.AchievementConstants.ACHIEVEMENT_PACKRAT,
                packratProgress
            )
        )

        return achievements
    }

    private fun loadTotalItemsCreated(): Int {
        val sharedPreferences = getSharedPreferences("Achievements", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("total_items_created", 0)
    }

    // Achievement data class
    data class Achievement(val name: String, val progress: Int) {
        val isAchieved: Boolean
            get() = progress >= getThreshold(name)

        fun getThreshold(name: String): Int {
            return when (name) {
                Achievements.AchievementConstants.ACHIEVEMENT_STARTER -> Achievements.AchievementConstants.THRESHOLD_STARTER
                Achievements.AchievementConstants.ACHIEVEMENT_COLLECTOR -> Achievements.AchievementConstants.THRESHOLD_COLLECTOR
                Achievements.AchievementConstants.ACHIEVEMENT_PACKRAT -> Achievements.AchievementConstants.THRESHOLD_PACKRAT
                else -> 0
            }
        }
    }

    // Custom RecyclerView Adapter
    class AchievementAdapter(
        private val achievements: List<Achievement>
    ) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_achievement, parent, false)
            return AchievementViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
            val achievement = achievements[position]
            holder.bind(achievement)
        }

        override fun getItemCount(): Int {
            return achievements.size
        }

        inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val nameTextView: TextView = itemView.findViewById(R.id.achievementName)
            private val progressTextView: TextView = itemView.findViewById(R.id.achievementProgress)
            private val statusTextView: TextView = itemView.findViewById(R.id.achievementStatus)
            private val image: ImageView = itemView.findViewById(R.id.achievementImage)
            private lateinit var progress: String
            private var archived = "Not Achieved"

            fun bind(achievement: Achievement) {

                when (achievement.name) {
                    "Starter" -> image.setImageResource(R.drawable.medal_bronze)
                    "Collector" -> image.setImageResource(R.drawable.medal_silver)
                    "Packrat" -> image.setImageResource(R.drawable.medal_gold)
                }

                nameTextView.text = achievement.name
                if (achievement.isAchieved) {
                    archived = "Achieved"
                    progress =
                        "Progress: ${achievement.getThreshold(achievement.name)}/${
                            achievement.getThreshold(
                                achievement.name
                            )
                        }"

                } else {
                    image.setColorFilter(
                        Color.parseColor("#888888"),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    progress =
                        "Progress: ${achievement.progress}/${achievement.getThreshold(achievement.name)}"
                }

                progressTextView.text = progress
                statusTextView.text = archived
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}