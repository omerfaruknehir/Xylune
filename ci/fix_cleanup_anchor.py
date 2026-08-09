from pathlib import Path

path = Path('ci/cleanup_architecture_once.py')
text = path.read_text(encoding='utf-8')
old = '''replace_once(
    onboarding,
    'Text("The built-in provider catalog is delayed. Setup remains usable and Xylune will keep retrying in the background.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)',
    'Text(stringResource(R.string.provider_catalog_initialization_failed), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)',
)
'''
new = '''replace_once(
    onboarding,
    \'''            Text(
                "The built-in provider catalog is delayed. Setup remains usable and Xylune will keep retrying in the background.",
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
\''',
    \'''            Text(
                stringResource(R.string.provider_catalog_initialization_failed),
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
\''',
)
'''
if text.count(old) != 1:
    raise SystemExit(f'expected exactly one old onboarding anchor, got {text.count(old)}')
path.write_text(text.replace(old, new), encoding='utf-8')
