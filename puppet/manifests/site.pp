# ==============================================================================
# Puppet Manifest: Target Node Configuration (Task 13)
# Project: Git-Based Farmer Training Registration Platform
# Author: Rutuja Kamale (Roll No: 23102A0028)
# ==============================================================================

node default {

  $app_name      = 'farmer-registration-platform'
  $app_version   = '1.0.0'
  $app_user      = 'farmerapp'
  $app_group     = 'farmerapp'
  $app_root      = '/opt/farmer-platform'
  $app_log_dir   = '/var/log/farmer-platform'
  $app_port      = 8085
  $spring_profile = 'prod'

  # 1. Ensure required system packages
  package { ['openjdk-21-jre-headless', 'curl', 'wget', 'ufw']:
    ensure => installed,
  }

  # 2. System Group & User
  group { $app_group:
    ensure => present,
    system => true,
  }

  user { $app_user:
    ensure     => present,
    gid        => $app_group,
    shell      => '/usr/sbin/nologin',
    home       => $app_root,
    managehome => false,
    system     => true,
    require    => Group[$app_group],
  }

  # 3. Directory Hierarchy
  file { [$app_root, "${app_root}/bin", "${app_root}/config"]:
    ensure  => directory,
    owner   => $app_user,
    group   => $app_group,
    mode    => '0755',
    require => User[$app_user],
  }

  file { $app_log_dir:
    ensure  => directory,
    owner   => $app_user,
    group   => $app_group,
    mode    => '0750',
    require => User[$app_user],
  }

  # 4. Systemd Service Unit
  file { '/etc/systemd/system/farmer-platform.service':
    ensure  => file,
    owner   => 'root',
    group   => 'root',
    mode    => '0644',
    content => "[Unit]\nDescription=Farmer Training Platform Service\nAfter=network.target\n\n[Service]\nType=simple\nUser=${app_user}\nGroup=${app_group}\nWorkingDirectory=${app_root}\nExecStart=/usr/bin/java -Dspring.profiles.active=${spring_profile} -Dserver.port=${app_port} -jar ${app_root}/bin/${app_name}-${app_version}.jar\nRestart=always\nRestartSec=10\nStandardOutput=append:${app_log_dir}/stdout.log\nStandardError=append:${app_log_dir}/stderr.log\n\n[Install]\nWantedBy=multi-user.target\n",
    notify  => Exec['systemd-daemon-reload'],
  }

  exec { 'systemd-daemon-reload':
    command     => '/bin/systemctl daemon-reload',
    refreshonly => true,
  }

  # 5. Service State Management
  service { 'farmer-platform':
    ensure  => running,
    enable  => true,
    require => [
      File['/etc/systemd/system/farmer-platform.service'],
      Package['openjdk-21-jre-headless'],
    ],
  }
}
