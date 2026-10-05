package com.example.pokemax

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemax.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSubmitted: (String, String) -> Unit,
    onGuestLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    PokedexFrameContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Authentic Pokémon 3D Styled Pokémax Logo
            PokemaxLogoText(
                fontSize = 48.sp,
                shadowOffset = 4.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PokedexBlueDark.copy(alpha = 0.75f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Acesse sua conta Pokédex",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = usernameOrEmail,
                onValueChange = { usernameOrEmail = it },
                label = { Text("Usuário ou E-mail", color = Color.White.copy(alpha = 0.85f)) },
                leadingIcon = { Text("👤", fontSize = 16.sp) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PokedexBlueDark.copy(alpha = 0.85f),
                    unfocusedContainerColor = PokedexBlueDark.copy(alpha = 0.75f),
                    focusedBorderColor = PokemonYellow,
                    unfocusedBorderColor = PokedexCyanAccent.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha", color = Color.White.copy(alpha = 0.85f)) },
                leadingIcon = { Text("🔒", fontSize = 16.sp) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Text(
                            text = if (isPasswordVisible) "👁️" else "🙈",
                            fontSize = 16.sp
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PokedexBlueDark.copy(alpha = 0.85f),
                    unfocusedContainerColor = PokedexBlueDark.copy(alpha = 0.75f),
                    focusedBorderColor = PokemonYellow,
                    unfocusedBorderColor = PokedexCyanAccent.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Pokémon Yellow Button with Blue 3D Text
            Button(
                onClick = { onLoginSubmitted(usernameOrEmail, password) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PokemonYellow),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "ENTRAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PokemonBlueDark,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGuestLogin,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = PokedexBlueDark.copy(alpha = 0.6f),
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, PokedexCyanAccent.copy(alpha = 0.8f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Acessar como Convidado",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Pronto para conexão com Banco de Dados",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
