#!/bin/bash
set -euo pipefail
cd "$(git rev-parse --show-toplevel)" || exit 1

red=$(tput setaf 1)
green=$(tput setaf 2)
yellow=$(tput setaf 3)
creset=$(tput sgr0)

commands=( "ktlint" "git-hooks")
packages=( "none")
taps=( "none" )
helpurls=( "ktlint:https://ktlint.github.io/" )

get::tapname() {
  for entry in "${taps[@]}"; do
    key="${entry%%:*}"
    value="${entry#*:}"
    if [ "$key" = "$1" ]; then
      echo "$value"
      return
    fi
  done
}

get::pkgname() {
  for entry in "${packages[@]}"; do
    key="${entry%%:*}"
    value="${entry#*:}"
    if [ "$key" = "$1" ]; then
      echo "$value"
      return
    fi
  done
  echo "$1"
}

get::installurl() {
  for entry in "${helpurls[@]}"; do
    key="${entry%%:*}"
    value="${entry#*:}"
    if [ "$key" = "$1" ]; then
      echo "$value"
      return
    fi
  done
  get::pkgname "$1"
}

ensure::tool() {
  local cmd cmdName pkg pkgurl
  cmd="$1"
  cmdName="${2-${1}}"
  pkg="${2-$(get::pkgname "$1")}"
  pkgurl="$(get::installurl "$1")"

  if ! command -v "$cmd" > /dev/null; then
    if command -v brew >/dev/null 2>&1; then
      tap="$(get::tapname $cmd)"
      if [ -n "$tap" ]; then
        echo "${yellow}Running 'brew tap ${tap}'...${creset}"
        brew tap "${tap}"
      fi
      echo "${yellow}Running 'brew install ${pkg}'...${creset}"
      brew install "${pkg}"
    else
      echo "${red}✗${creset} You have to install ${pkgurl}"
      exit 1
    fi
  else
    echo "${green}✓${creset} Found ${cmdName}"
  fi
}

for cmd in "${commands[@]}"; do
  ensure::tool "$cmd"
done
