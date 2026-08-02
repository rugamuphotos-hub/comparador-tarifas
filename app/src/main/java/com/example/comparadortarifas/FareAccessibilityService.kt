package com.example.comparadortarifas

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class FareAccessibilityService : AccessibilityService() {

    // Busca importes tipo "12,34 €", "12.34€", "€ 12,34"
    private val priceRegex = Regex("""\d{1,3}[.,]\d{2}\s?€|€\s?\d{1,3}[.,]\d{2}""")

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        val label = when (pkg) {
            "com.ubercab" -> "Uber"
            "ee.mtakso.client" -> "Bolt"
            "com.cabify.rider" -> "Cabify"
            else -> return
        }

        val root = rootInActiveWindow ?: return
        val textsFound = mutableSetOf<String>()
        collectText(root, textsFound)
        root.recycle()

        val price = textsFound.firstNotNullOfOrNull { priceRegex.find(it)?.value }
        if (price != null) {
            FareStore.update(label, price)
        }
    }

    private fun collectText(node: AccessibilityNodeInfo, out: MutableSet<String>) {
        node.text?.let {
            if (it.isNotBlank()) out.add(it.toString())
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectText(child, out)
            child.recycle()
        }
    }

    override fun onInterrupt() {}
}
