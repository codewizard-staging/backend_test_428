package com.replicacia;

import com.replicacia.model.AppUser;
import com.replicacia.model.HostInfo;
import com.replicacia.model.Permission;
import com.replicacia.model.Role;
import com.replicacia.rest.admin.dto.HostInfoResponseDTO;
import com.replicacia.rest.admin.dto.PermissionResponseDTO;
import com.replicacia.rest.admin.dto.RoleResponseDTO;
import com.replicacia.rest.security.dto.UserRegisterResponseDTO;
import com.replicacia.rest.security.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Gateway {

  public static void main(final String[] args) {
    SpringApplication.run(Gateway.class, args);
  }

  @Bean
  public ModelMapper getModelMapper() {
    final ModelMapper mapper = new ModelMapper();
    mapper.addMappings(
        new PropertyMap<AppUser, UserResponseDTO>() {
          @Override
          protected void configure() {
            this.map().setUserId(this.source.getPublicId());
          }
        });
    mapper.addMappings(
        new PropertyMap<AppUser, UserRegisterResponseDTO>() {
          @Override
          protected void configure() {
            this.map().setUserId(this.source.getPublicId());
          }
        });
    mapper.addMappings(
        new PropertyMap<Role, RoleResponseDTO>() {
          @Override
          protected void configure() {
            this.map().setId(this.source.getPublicId());
          }
        });
    mapper.addMappings(
        new PropertyMap<HostInfo, HostInfoResponseDTO>() {
          @Override
          protected void configure() {
            this.map().setId(this.source.getPublicId());
          }
        });
    mapper.addMappings(
        new PropertyMap<Permission, PermissionResponseDTO>() {
          @Override
          protected void configure() {
            this.map().setId(this.source.getPublicId());
          }
        });
    return mapper;
  }
}
