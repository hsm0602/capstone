package com.example.myfirstkotlinapp.exercise

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.sqrt
import kotlin.math.acos
import kotlin.math.PI

object ExerciseLogic {
    private var isDown = false
    private var lowFrameCount = 0
    private var highFrameCount = 0
    private var lastCountTime = 0L
    private const val Cooldown = 500L // ms
    private var Count = 0


    fun countSquat(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false

        val leftHipY = landmarks[23].y()
        val rightHipY = landmarks[24].y()
        val hipY = (leftHipY + rightHipY) / 2

        val leftKneeY = landmarks[25].y()
        val rightKneeY = landmarks[26].y()
        val kneeY = (leftKneeY + rightKneeY) / 2

        val currentTime = System.currentTimeMillis()

        val hipBelowKnee = hipY > kneeY + 0.02f
        val hipAboveKnee = hipY < kneeY - 0.02f

        if (hipBelowKnee) {
            lowFrameCount++
            highFrameCount = 0
            if (lowFrameCount >= 3) {
                isDown = true
            }
        } else if (hipAboveKnee && isDown) {
            highFrameCount++
            if (highFrameCount >= 3 && currentTime - lastCountTime > Cooldown) {
                lastCountTime = currentTime
                lowFrameCount = 0
                highFrameCount = 0
                isDown = false
                Count++
                return true
            }
        } else {
            lowFrameCount = 0
            highFrameCount = 0
        }

        return false
    }


    fun countPushup(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false

        val leftShoulderY = landmarks[11].y()
        val rightShoulderY = landmarks[12].y()
        val shoulderY = (leftShoulderY + rightShoulderY) / 2

        val leftElbowY = landmarks[13].y()
        val rightElbowY = landmarks[14].y()
        val elbowY = (leftElbowY + rightElbowY) / 2

        val currentTime = System.currentTimeMillis()

        // 다운: 어깨와 팔꿈치 y좌표가 거의 같을 때 (팔꿈치가 어깨 옆에 온 상태)
        val downPose = Math.abs(shoulderY - elbowY) < 0.02f

        // 업: 어깨가 팔꿈치보다 충분히 위에 있을 때 (팔을 편 상태)
        val upPose = shoulderY < elbowY - 0.025f

        if (downPose) {
            lowFrameCount++
            highFrameCount = 0
            if (lowFrameCount >= 3) {
                isDown = true
            }
        } else if (upPose && isDown) {
            highFrameCount++
            if (highFrameCount >= 3 && currentTime - lastCountTime > Cooldown) {
                lastCountTime = currentTime
                lowFrameCount = 0
                highFrameCount = 0
                isDown = false
                Count++
                return true
            }
        } else {
            lowFrameCount = 0
            highFrameCount = 0
        }

        return false
    }

    fun countPullup(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false

        val leftWristY = landmarks[15].y()
        val rightWristY = landmarks[16].y()
        val wristY = (leftWristY + rightWristY) / 2

        val leftShoulderY = landmarks[11].y()
        val rightShoulderY = landmarks[12].y()
        val shoulderY = (leftShoulderY + rightShoulderY) / 2

        val currentTime = System.currentTimeMillis()
        val shoulderToWristDist = shoulderY - wristY

        // 기준값은 실제 실험하며 조정 필요 (예: 0.07f, 0.13f)
        val upPose = shoulderToWristDist < 0.07f
        val downPose = shoulderToWristDist > 0.13f

        if (upPose) {
            lowFrameCount++
            highFrameCount = 0
            if (lowFrameCount >= 3) {
                isDown = true
            }
        } else if (downPose && isDown) {
            highFrameCount++
            if (highFrameCount >= 3 && currentTime - lastCountTime > Cooldown) {
                lastCountTime = currentTime
                lowFrameCount = 0
                highFrameCount = 0
                isDown = false
                Count++
                return true
            }
        } else {
            lowFrameCount = 0
            highFrameCount = 0
        }

        return false
    }

    fun countShoulderPress(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false
        val currentTime = System.currentTimeMillis()

        val leftShoulderY = landmarks[11].y()
        val rightShoulderY = landmarks[12].y()
        val shoulderY = (leftShoulderY + rightShoulderY) / 2

        val leftWristY = landmarks[15].y()
        val rightWristY = landmarks[16].y()
        val wristY = (leftWristY + rightWristY) / 2

        val leftElbowY = landmarks[13].y()
        val rightElbowY = landmarks[14].y()
        val elbowY = (leftElbowY + rightElbowY) / 2

        val isPushedUp = wristY < shoulderY - 0.02f && elbowY < shoulderY - 0.01f
        val isLowered = elbowY > shoulderY + 0.03f

        if (isLowered && !isDown) {
            isDown = true
        }

        if (isPushedUp && isDown && currentTime - lastCountTime > 400L) {
            isDown = false
            lastCountTime = currentTime
            return true
        }

        return false
    }

    fun countLegRaise(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false

        // 엉덩이, 무릎, 발목 좌표
        val leftHip = landmarks[23]
        val rightHip = landmarks[24]
        val leftKnee = landmarks[25]
        val rightKnee = landmarks[26]
        val leftAnkle = landmarks[27]
        val rightAnkle = landmarks[28]

        // 양쪽 다리의 각도 계산 (엉덩이-무릎-발목 각도)
        val leftLegAngle = calculateAngle(
            leftHip.x(), leftHip.y(),
            leftKnee.x(), leftKnee.y(),
            leftAnkle.x(), leftAnkle.y()
        )
        val rightLegAngle = calculateAngle(
            rightHip.x(), rightHip.y(),
            rightKnee.x(), rightKnee.y(),
            rightAnkle.x(), rightAnkle.y()
        )

        // 평균 다리 각도
        val avgLegAngle = (leftLegAngle + rightLegAngle) / 2

        val currentTime = System.currentTimeMillis()

        // 다리가 올라간 상태 (각도가 작을 때 - 다리가 몸통에 가까워짐)
        val legsUp = avgLegAngle < 100.0
        // 다리가 내려간 상태 (각도가 클 때)
        val legsDown = avgLegAngle > 140.0

        if (legsUp) {
            lowFrameCount++
            highFrameCount = 0
            if (lowFrameCount >= 5) {
                isDown = true // 다리 올린 상태를 low로 간주.
            }
        } else if (legsDown && isDown) {
            highFrameCount++
            if (highFrameCount >= 5 && currentTime - lastCountTime > 800L) {
                lastCountTime = currentTime
                highFrameCount = 0
                lowFrameCount = 0
                isDown = false
                Count++
                return true
            }
        } else {
            if (highFrameCount > 0) highFrameCount--
            if (lowFrameCount > 0) lowFrameCount--
        }

        return false
    }

    // 세 점으로 각도를 계산하는 함수
    private fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Double {
        val vector1X = x1 - x2
        val vector1Y = y1 - y2
        val vector2X = x3 - x2
        val vector2Y = y3 - y2

        val dot = vector1X * vector2X + vector1Y * vector2Y
        val magnitude1 = sqrt((vector1X * vector1X + vector1Y * vector1Y).toDouble())
        val magnitude2 = sqrt((vector2X * vector2X + vector2Y * vector2Y).toDouble())

        val cosAngle = dot / (magnitude1 * magnitude2)
        val angle = acos(cosAngle.coerceIn(-1.0, 1.0)) * 180.0 / PI

        return angle
    }

    fun resetCount() {
        Count = 0
        isDown = false
        lowFrameCount = 0
        highFrameCount = 0
        lastCountTime = 0L
    }
}