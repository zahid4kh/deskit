/*
Copyright 2025 Zahid Khalilov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package deskit.dialogs.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Represents the color scheme for ConfirmationDialog components.
 *
 * @param iconTint Color applied to the dialog icon
 * @param confirmButtonColor Container color for the confirm button
 * @param confirmButtonTextColor Text color for the confirm button
 * @param cancelButtonColor Container color for the cancel button
 * @param cancelButtonTextColor Text color for the cancel button
 */
data class ConfirmationDialogColors(
    val iconTint: Color,
    val confirmButtonColor: Color,
    val confirmButtonTextColor: Color,
    val cancelButtonColor: Color,
    val cancelButtonTextColor: Color
)

/**
 * Contains default values for ConfirmationDialog colors.
 */
object ConfirmationDialogDefaults{
    /**
     * Creates a [ConfirmationDialogColors] instance with the provided color values.
     * Any unspecified colors will use the default Material3 theme colors.
     */
    @Composable
    fun colors(
        iconTint: Color = MaterialTheme.colorScheme.primary,
        confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
        confirmButtonTextColor: Color = MaterialTheme.colorScheme.onPrimary,
        cancelButtonColor: Color = MaterialTheme.colorScheme.errorContainer,
        cancelButtonTextColor: Color = MaterialTheme.colorScheme.onErrorContainer
    ): ConfirmationDialogColors = ConfirmationDialogColors(
        iconTint = iconTint,
        confirmButtonColor = confirmButtonColor,
        confirmButtonTextColor = confirmButtonTextColor,
        cancelButtonColor = cancelButtonColor,
        cancelButtonTextColor = cancelButtonTextColor
    )
}