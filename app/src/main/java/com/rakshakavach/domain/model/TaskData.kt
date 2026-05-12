package com.rakshakavach.domain.model

data class TaskModel(
    val taskId: String,
    val taskName: LocalizedString,
    val mandatoryPPE: List<PPEItem>,
    val riskWeight: Map<PPEItem, Int>
)

data class LocalizedString(
    val english: String,
    val hindi: String
)

object TaskDataSource {
    val tasks = listOf(
        TaskModel(
            taskId = "T001",
            taskName = LocalizedString("Welding", "वेल्डिंग"),
            mandatoryPPE = listOf(PPEItem.HELMET, PPEItem.GLOVES, PPEItem.BOOTS, PPEItem.FACE_SHIELD, PPEItem.APRON),
            riskWeight = mapOf(
                PPEItem.HELMET to 80,
                PPEItem.GLOVES to 60,
                PPEItem.BOOTS to 50,
                PPEItem.FACE_SHIELD to 90,
                PPEItem.APRON to 40
            )
        ),
        TaskModel(
            taskId = "T002",
            taskName = LocalizedString("Height Work", "ऊंचाई का काम"),
            mandatoryPPE = listOf(PPEItem.HELMET, PPEItem.BOOTS, PPEItem.HARNESS),
            riskWeight = mapOf(
                PPEItem.HELMET to 90,
                PPEItem.BOOTS to 40,
                PPEItem.HARNESS to 100
            )
        ),
        TaskModel(
            taskId = "T003",
            taskName = LocalizedString("Trench Digging", "खाई खोदना"),
            mandatoryPPE = listOf(PPEItem.HELMET, PPEItem.BOOTS, PPEItem.GLOVES, PPEItem.VEST),
            riskWeight = mapOf(
                PPEItem.HELMET to 70,
                PPEItem.BOOTS to 60,
                PPEItem.GLOVES to 40,
                PPEItem.VEST to 30
            )
        ),
        TaskModel(
            taskId = "T004",
            taskName = LocalizedString("Electrical Work", "बिजली का काम"),
            mandatoryPPE = listOf(PPEItem.HELMET, PPEItem.GLOVES, PPEItem.BOOTS, PPEItem.GOGGLES),
            riskWeight = mapOf(
                PPEItem.HELMET to 60,
                PPEItem.GLOVES to 100, // Critical for electrical
                PPEItem.BOOTS to 80,
                PPEItem.GOGGLES to 40
            )
        ),
        TaskModel(
            taskId = "T005",
            taskName = LocalizedString("Heavy Machinery", "भारी मशीनरी"),
            mandatoryPPE = listOf(PPEItem.HELMET, PPEItem.BOOTS, PPEItem.VEST, PPEItem.EARPLUGS, PPEItem.GLOVES),
            riskWeight = mapOf(
                PPEItem.HELMET to 80,
                PPEItem.BOOTS to 70,
                PPEItem.VEST to 50,
                PPEItem.EARPLUGS to 60,
                PPEItem.GLOVES to 30
            )
        ),
        TaskModel(
            taskId = "T006",
            taskName = LocalizedString("Chemical Handling", "रसायन प्रबंधन"),
            mandatoryPPE = listOf(PPEItem.GLOVES, PPEItem.GOGGLES, PPEItem.RESPIRATOR, PPEItem.APRON, PPEItem.BOOTS),
            riskWeight = mapOf(
                PPEItem.GLOVES to 80,
                PPEItem.GOGGLES to 90,
                PPEItem.RESPIRATOR to 100,
                PPEItem.APRON to 70,
                PPEItem.BOOTS to 50
            )
        ),
        TaskModel(
            taskId = "T007",
            taskName = LocalizedString("Painting", "रंग-रोगन"),
            mandatoryPPE = listOf(PPEItem.RESPIRATOR, PPEItem.GLOVES, PPEItem.GOGGLES),
            riskWeight = mapOf(
                PPEItem.RESPIRATOR to 80,
                PPEItem.GLOVES to 40,
                PPEItem.GOGGLES to 60
            )
        ),
        TaskModel(
            taskId = "T008",
            taskName = LocalizedString("Demolition", "विध्वंस"),
            mandatoryPPE = listOf(PPEItem.HELMET, PPEItem.BOOTS, PPEItem.GLOVES, PPEItem.GOGGLES, PPEItem.RESPIRATOR, PPEItem.EARPLUGS),
            riskWeight = mapOf(
                PPEItem.HELMET to 100,
                PPEItem.BOOTS to 80,
                PPEItem.GLOVES to 60,
                PPEItem.GOGGLES to 70,
                PPEItem.RESPIRATOR to 90,
                PPEItem.EARPLUGS to 50
            )
        ),
        TaskModel(
            taskId = "T009",
            taskName = LocalizedString("Carpentry", "बढ़ई का काम"),
            mandatoryPPE = listOf(PPEItem.GOGGLES, PPEItem.GLOVES, PPEItem.EARPLUGS, PPEItem.BOOTS),
            riskWeight = mapOf(
                PPEItem.GOGGLES to 80,
                PPEItem.GLOVES to 50,
                PPEItem.EARPLUGS to 60,
                PPEItem.BOOTS to 40
            )
        ),
        TaskModel(
            taskId = "T010",
            taskName = LocalizedString("Loading/Unloading", "लोडिंग/अनलोडिंग"),
            mandatoryPPE = listOf(PPEItem.HELMET, PPEItem.BOOTS, PPEItem.GLOVES, PPEItem.VEST),
            riskWeight = mapOf(
                PPEItem.HELMET to 70,
                PPEItem.BOOTS to 90,
                PPEItem.GLOVES to 60,
                PPEItem.VEST to 40
            )
        )
    )
}
