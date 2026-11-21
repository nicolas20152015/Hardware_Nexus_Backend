package cl.duocuc.hardwarenexus.service;

import cl.duocuc.hardwarenexus.dto.request.LoginRequestDTO;
import cl.duocuc.hardwarenexus.dto.request.RegisterRequestDTO;
import cl.duocuc.hardwarenexus.dto.response.LoginResponseDTO;
import cl.duocuc.hardwarenexus.dto.response.UsuarioResponseDTO;
import cl.duocuc.hardwarenexus.model.Rol;
import cl.duocuc.hardwarenexus.model.Usuario;
import cl.duocuc.hardwarenexus.repository.RolRepository;
import cl.duocuc.hardwarenexus.repository.UsuarioRepository;
import cl.duocuc.hardwarenexus.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponseDTO registrar(RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email '" + request.getEmail() + "' ya está registrado");
        }

        String nombreRol = (request.getRol() != null && !request.getRol().trim().isEmpty())
                ? request.getRol().toUpperCase()
                : "CLIENTE";

        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol '" + nombreRol + "' no es válido"));

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .rol(rol)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(guardado.getEmail())
                .password(guardado.getPassword())
                .roles(guardado.getRol().getNombre())
                .build();

        String token = jwtService.generateToken(
                userDetails,
                guardado.getId(),
                guardado.getRol().getNombre(),
                guardado.getNombre() + " " + guardado.getApellido()
        );

        return LoginResponseDTO.builder()
                .token(token)
                .tipoToken("Bearer")
                .id(guardado.getId())
                .nombre(guardado.getNombre())
                .apellido(guardado.getApellido())
                .email(guardado.getEmail())
                .rol(guardado.getRol().getNombre())
                .build();
    }

    public LoginResponseDTO autenticar(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String token = jwtService.generateToken(
                userDetails,
                usuario.getId(),
                usuario.getRol().getNombre(),
                usuario.getNombre() + " " + usuario.getApellido()
        );

        return LoginResponseDTO.builder()
                .token(token)
                .tipoToken("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol().getNombre())
                .build();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + email));
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
