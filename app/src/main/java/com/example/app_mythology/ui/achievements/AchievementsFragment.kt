package com.example.app_mythology.ui.achievements

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.app_mythology.R
import com.example.app_mythology.achievements.Achievement
import com.example.app_mythology.achievements.AchievementCatalog
import com.example.app_mythology.achievements.AchievementManager

class AchievementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_achievements, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<LinearLayout>(R.id.container_achievements)
        val unlocked = AchievementManager.unlockedIds()

        var lastCategory: String? = null
        AchievementCatalog.all.forEach { achievement ->
            if (achievement.category != lastCategory) {
                lastCategory = achievement.category
                container.addView(categoryHeader(achievement.category))
            }
            container.addView(achievementItem(container, achievement, achievement.id in unlocked))
        }
    }

    private fun categoryHeader(title: String): TextView = TextView(requireContext()).apply {
        text = title
        textSize = 16f
        setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        setPadding(0, dpToPx(12), 0, dpToPx(8))
    }

    private fun achievementItem(parent: ViewGroup, achievement: Achievement, unlocked: Boolean): View {
        val item = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_achievement, parent, false)

        val ivIcon = item.findViewById<ImageView>(R.id.iv_achievement_icon)
        val tvName = item.findViewById<TextView>(R.id.tv_achievement_name)
        val tvMethod = item.findViewById<TextView>(R.id.tv_achievement_method)

        ivIcon.setImageDrawable(loadIcon(achievement.id))
        tvName.text = achievement.name

        if (unlocked) {
            item.setBackgroundResource(R.drawable.bg_achievement_unlocked)
            val textColor = ContextCompat.getColor(requireContext(), R.color.on_primary)
            tvName.setTextColor(textColor)
            tvMethod.setTextColor(textColor)
            tvMethod.text = achievement.method
            tvMethod.isVisible = true
        } else {
            item.setBackgroundResource(R.drawable.bg_achievement_locked)
            tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface))
            tvMethod.isVisible = false
        }

        return item
    }

    /** Charge l'icône déposée par l'utilisateur dans assets/achievements/<id>.png, ou un blason générique à défaut. */
    private fun loadIcon(id: String): Drawable? {
        val stream = try {
            requireContext().assets.open("achievements/$id.png")
        } catch (e: Exception) {
            null
        }
        return if (stream != null) {
            stream.use { Drawable.createFromStream(it, id) }
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_achievement_placeholder)
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
