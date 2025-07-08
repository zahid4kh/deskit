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
 * Represents the color scheme for InfoDialog components.
 *
 * @param iconTint Color applied to the dialog icon
 * @param okButtonColor Container color for the OK button
 * @param okButtonTextColor Text color for the OK button
 */
data class InfoDialogColors(
    val iconTint: Color,
    val okButtonColor: Color,
    val okButtonTextColor: Color
)

/**
 * Contains default values for InfoDialog colors.
 */
object InfoDialogDefaults{
    /**
     * Creates a [InfoDialogColors] instance with the provided color values.
     * Any unspecified colors will use the default Material3 theme colors.
     */
    @Composable
    fun colors(
        iconTint: Color = MaterialTheme.colorScheme.primary,
        okButtonColor: Color = MaterialTheme.colorScheme.primary,
        okButtonTextColor: Color = MaterialTheme.colorScheme.onPrimary
    ): InfoDialogColors = InfoDialogColors(
        iconTint = iconTint,
        okButtonColor = okButtonColor,
        okButtonTextColor = okButtonTextColor
    )
}