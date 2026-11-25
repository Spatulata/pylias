package com.example.pylias.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ModuleRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getModules(): MutableList<WordModule> {
        val raw = prefs.getString(KEY_MODULES, null)
        if (raw.isNullOrBlank()) {
            val seed = defaultModules()
            saveModules(seed)
            return seed
        }
        return parseModules(raw)
    }

    fun getModule(id: String): WordModule? = getModules().find { it.id == id }

    fun saveModule(module: WordModule) {
        val modules = getModules()
        val index = modules.indexOfFirst { it.id == module.id }
        if (index >= 0) {
            modules[index] = module
        } else {
            modules.add(module)
        }
        saveModules(modules)
    }

    fun deleteModule(id: String) {
        val modules = getModules().filterNot { it.id == id }.toMutableList()
        saveModules(modules)
    }

    fun createModule(name: String, words: List<String> = emptyList()): WordModule {
        val module = WordModule(
            id = UUID.randomUUID().toString(),
            name = name.trim().ifBlank { "Новый модуль" },
            words = words.map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
        )
        saveModule(module)
        return module
    }

    private fun saveModules(modules: List<WordModule>) {
        val array = JSONArray()
        modules.forEach { module ->
            array.put(
                JSONObject().apply {
                    put("id", module.id)
                    put("name", module.name)
                    put("words", JSONArray(module.words))
                }
            )
        }
        prefs.edit().putString(KEY_MODULES, array.toString()).apply()
    }

    private fun parseModules(raw: String): MutableList<WordModule> {
        val array = JSONArray(raw)
        val result = mutableListOf<WordModule>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val wordsArray = obj.optJSONArray("words") ?: JSONArray()
            val words = mutableListOf<String>()
            for (j in 0 until wordsArray.length()) {
                words.add(wordsArray.getString(j))
            }
            result.add(
                WordModule(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    words = words
                )
            )
        }
        return result
    }

    private fun defaultModules(): MutableList<WordModule> = mutableListOf(
        WordModule(
            id = UUID.randomUUID().toString(),
            name = "Python Basics",
            words = mutableListOf(
                "def", "list", "dict", "tuple", "set", "class", "import",
                "lambda", "range", "len", "print", "return", "if", "elif",
                "else", "for", "while", "try", "except", "with", "open",
                "str", "int", "float", "bool", "True", "False", "None",
                "append", "pop", "sort", "map", "filter", "zip"
            )
        )
    )

    companion object {
        private const val PREFS = "pylias_prefs"
        private const val KEY_MODULES = "modules"

        @Volatile
        private var instance: ModuleRepository? = null

        fun get(context: Context): ModuleRepository =
            instance ?: synchronized(this) {
                instance ?: ModuleRepository(context.applicationContext).also { instance = it }
            }
    }
}
